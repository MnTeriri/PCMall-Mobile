# 电脑配件商城 Android 客户端（PCMallCompose）

## 项目说明

PCMallCompose 是 PCMall 电脑配件商城的 Android 原生客户端，采用 Kotlin + Jetpack Compose 构建。作为 PCMall 微服务后端的前端消费端，实现了完整的移动端购物体验，包括：

* 用户登录注册（含图形验证码）
* 首页广告轮播与商品瀑布流浏览
* 商品搜索与分类浏览
* 商品详情查看与加入购物车
* 购物车管理（增减数量、单选/全选、删除）
* 收货地址管理（省市区联动选择、新增、修改、删除）
* 订单管理（按状态分页查看、支付、取消、退款、确认收货）
* AI 购物助手（SSE 流式对话、商品推荐、Markdown 渲染）
* 基于 Token 的自动鉴权与会话持久化
* 离线缓存与分页加载（Room + Paging 3 + RemoteMediator）

后端项目说明：[PCMall](https://github.com/MnTeriri/PCMall)

## 使用的框架

* Kotlin
* Jetpack Compose（声明式 UI）
* Material Design 3 
* Jetpack Navigation Compose（类型安全路由）
* Hilt（依赖注入）
* Room（本地数据库 + 离线缓存）
* Paging + RemoteMediator（分页加载 + 网络与本地数据协调）
* Retrofit + OkHttp（HTTP 客户端 + Cookie 持久化 + SSE）
* Glide（图片加载）
* ...

## 项目结构

```
PCMallCompose
├── app/                         // 主应用模块（UI 页面、ViewModel、Application）
├── core/
│   ├── common/                  // 通用模块
│   ├── model/                   // 数据模型
│   ├── network/                 // 网络层
│   ├── database/                // 数据库层（Room）
│   └── data/                    // 数据仓库层
├── gradle/
│   ├── libs.versions.toml       // 版本目录（统一依赖管理）
│   └── wrapper/
├── build.gradle.kts             // 根构建脚本
├── settings.gradle.kts          // 模块配置
```

## 架构说明

项目采用 MVVM 架构，核心数据流如下：

```
UI (Compose Page)
   │  StateFlow<UiState>
   ▼
ViewModel
   │  分页列表: Pager(RemoteMediator, PagingSource)
   │  单次操作: Retrofit 直接调用
   │
   ├──► RemoteMediator ──► Retrofit ──► PCMall 后端网关 (10000)
   │         │
   │         └───► Room (本地缓存)
   │                 ▲
   └──► PagingSource ┘  (分页场景下从 Room 读取)
```

- **UI 层**：Compose 页面通过 `collectAsStateWithLifecycle()` 订阅 ViewModel 的 `StateFlow`，自动响应数据变化。
- **ViewModel 层**：通过 Hilt 注入依赖，管理 UI 状态。列表数据通过创建 Pager 对象驱动分页加载，增删改等操作通过 Retrofit 直接发起请求（如 CartViewModel 的加减数量在 API 成功后同步更新本地 Room，实现乐观更新）
- **数据层**：分页场景采用 Paging 的 `RemoteMediator` 模式——网络数据由 `RemoteMediator` 写入 Room，`PagingSource` 从 Room 读取并暴露为 `Flow<PagingData>`，以 Room 为单一数据源实现离线缓存与无缝分页。非分页场景（登录、注册等）直接走 Retrofit 请求-响应。
- **网络层**：Retrofit + OkHttp，通过 `HeaderInterceptor` 自动附加 Token，`PersistentCookieJar` 持久化 Cookie（支持验证码等场景）。
- **鉴权**：基于 JWT Token。登录成功后将 Token 存入 SharedPreferences，所有请求自动携带；应用启动时检查"记住我"状态决定是否清除登录态。

### AI 对话流程

一次完整的 AI 对话流程如下：

```
用户输入 "推荐一款 4070 显卡"
        │
        ▼
AiChatViewModel.chat()
        │
        ├─ ① Room: 写入用户消息 (type=USER, status=FINISH)
        ├─ ② Room: 写入占位 AI 实体 (type=AI, status=CHATTING, content="")
        │
        ├─ ③ 发起 POST /ai/assistant/chat ──────────────► PCMall AI 模块
        │                                                    │
        │                                            LangChain4j + DeepSeek
        │                                               Milvus 向量检索
        │                                                    │
        ├─ ④ SSE 事件流 ◄────────────────────────────────────┘
        │     │
        │     ├─ TEXT 事件  → 拼接到 StringBuilder → 更新 UiState.streamingContent
        │     │                  │
        │     │                  └──► AiChatPage UI 实时逐字渲染
        │     │
        │     └─ GOODS 事件 → 解析商品列表 → 存入占位实体的 recommends 字段
        │
        └─ ⑤ SSE 流结束 → Room: 更新占位实体 (content=完整文本, status=FINISH)
                │
                ▼
        AiChatPage 展示完整回复（文本 + 推荐商品卡片）
```

对话历史通过 Room 持久化，`AiChatViewModel.chatHistoryPagingFlow` 以 Paging 方式分页读取，保证长会话的流畅滚动。
### 配置后端地址

修改 `NetworkModule.kt` 中的 `BASE_URL` 为你部署的后端网关地址：

```kotlin
// core/network/src/main/java/.../di/NetworkModule.kt
const val BASE_URL = "http://<your-server>:10000/api/"
```
