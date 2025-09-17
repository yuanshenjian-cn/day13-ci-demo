# Railway环境配置总结

## 🚀 Railway架构理解

基于你的发现，Railway的配置架构是：

```
一个Railway Project
├── PROJECT_ID: 全局唯一 (abc123)
├── Dev Environment
│   ├── TOKEN: dev专用token (token_dev_xxx)
│   └── SERVICE_ID: dev服务ID (service_dev_yyy)
├── UAT Environment  
│   ├── TOKEN: uat专用token (token_uat_xxx)
│   └── SERVICE_ID: uat服务ID (service_uat_yyy)
└── PROD Environment
    ├── TOKEN: prod专用token (token_prod_xxx)
    └── SERVICE_ID: prod服务ID (service_prod_yyy)
```

## 📋 GitHub Secrets配置

### Repository级别 (全局共享):
```
RAILWAY_PROJECT_ID = abc123
```

### Environment级别 (每个环境独立):

#### dev环境:
```
RAILWAY_TOKEN = token_dev_xxx
RAILWAY_SERVICE_ID = service_dev_yyy  
SERVICE_BASE_URL = https://your-dev.railway.app
```

#### uat环境:
```
RAILWAY_TOKEN = token_uat_xxx
RAILWAY_SERVICE_ID = service_uat_yyy
SERVICE_BASE_URL = https://your-uat.railway.app
```

#### prod环境:
```
RAILWAY_TOKEN = token_prod_xxx
RAILWAY_SERVICE_ID = service_prod_yyy
SERVICE_BASE_URL = https://your-prod.railway.app
```

## ⚙️ GitHub Actions工作原理

当workflow运行时：

```yaml
# main分支 (自动部署到dev)
environment: dev
# 自动使用:
# - RAILWAY_PROJECT_ID (global)
# - RAILWAY_TOKEN (dev环境的token)
# - RAILWAY_SERVICE_ID (dev环境的service)

# release分支 (手动部署)
environment: uat  # 第一步
# 自动使用:
# - RAILWAY_PROJECT_ID (global) 
# - RAILWAY_TOKEN (uat环境的token)
# - RAILWAY_SERVICE_ID (uat环境的service)

environment: prod  # 第二步
# 自动使用:
# - RAILWAY_PROJECT_ID (global)
# - RAILWAY_TOKEN (prod环境的token) 
# - RAILWAY_SERVICE_ID (prod环境的service)
```

## 🔧 获取Railway配置值

### 1. 获取PROJECT_ID:
- 进入Railway Dashboard
- 项目URL中的ID: `https://railway.app/project/[PROJECT_ID]`

### 2. 获取各环境TOKEN:
- Railway Dashboard → 切换到对应环境
- Settings → Tokens → 创建新token
- 每个环境都需要创建独立的token

### 3. 获取各环境SERVICE_ID:
- Railway Dashboard → 切换到对应环境
- 选择对应的服务
- Settings → 服务URL中包含SERVICE_ID

### 4. 获取各环境SERVICE_URL:
- Railway Dashboard → 切换到对应环境
- 服务的公网访问地址

## ✅ 配置验证

配置完成后，可以通过以下方式验证：

1. **Repository Secrets**: 应该只有 `RAILWAY_PROJECT_ID`
2. **Environment Secrets**: 每个环境都有3个secrets
3. **Railway CLI测试**: 在本地使用不同token测试连接

```bash
# 测试dev环境
export RAILWAY_TOKEN=token_dev_xxx
railway status

# 测试uat环境  
export RAILWAY_TOKEN=token_uat_xxx
railway status
```

## 🚨 常见错误

❌ **错误配置**: 
```
# 把token配置成全局的
Repository Secrets:
- RAILWAY_TOKEN (这是错误的)
```

✅ **正确配置**:
```
# token应该在每个environment中配置
Environment Secrets (dev):
- RAILWAY_TOKEN (dev专用)

Environment Secrets (uat):  
- RAILWAY_TOKEN (uat专用)
```

这样的配置确保了环境隔离和安全性！
