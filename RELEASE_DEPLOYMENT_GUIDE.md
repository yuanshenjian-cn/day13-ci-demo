# Release分支部署配置指南

## 概览

现在你有两个CI/CD流程：
- **main分支**: 自动部署到Dev环境
- **release分支**: 手动批准部署到UAT → PROD环境

## 工作流程设计

```
release分支推送 → test → sonar → deploy-uat (手动批准) → deploy-prod (手动批准)
```

## 1. GitHub Environment配置

需要在GitHub仓库设置中创建三个Environment：

### 步骤1：创建Environment
1. 进入 `Settings` → `Environments`
2. 点击 `New environment`
3. 分别创建：`dev`、`uat` 和 `prod`

### 步骤2：配置Environment Protection Rules

对于 **dev** 环境：
```
☐ Required reviewers: 无（自动部署）
☐ Wait timer: 0 minutes
```

对于 **uat** 环境：
```
☑️ Required reviewers: 选择需要批准的人员（至少1人）
☑️ Wait timer: 0 minutes（可选）
☑️ Prevent administrators from bypassing configured protection rules（推荐）
```

对于 **prod** 环境：
```
☑️ Required reviewers: 选择需要批准的人员（至少1人，建议2人）
☑️ Wait timer: 5 minutes（推荐，给予思考时间）
☑️ Prevent administrators from bypassing configured protection rules（强烈推荐）
```

### 步骤3：配置Environment Secrets
在每个environment中配置特定的secrets：

#### DEV环境 (`dev`) 的Environment Secrets:
```
RAILWAY_TOKEN=your_dev_railway_token
RAILWAY_SERVICE_ID=your_dev_service_id
SERVICE_BASE_URL=https://your-dev-service.railway.app
```

#### UAT环境 (`uat`) 的Environment Secrets:
```
RAILWAY_TOKEN=your_uat_railway_token
RAILWAY_SERVICE_ID=your_uat_service_id
SERVICE_BASE_URL=https://your-uat-service.railway.app
```

#### PROD环境 (`prod`) 的Environment Secrets:
```
RAILWAY_TOKEN=your_prod_railway_token
RAILWAY_SERVICE_ID=your_prod_service_id  
SERVICE_BASE_URL=https://your-prod-service.railway.app
```

## 2. GitHub Repository Secrets配置

在 `Settings` → `Secrets and variables` → `Actions` → `Repository secrets` 中添加：

### 全局 Railway Secrets:
```
RAILWAY_PROJECT_ID=your_project_id  # 整个项目共用一个ID
```

### 获取这些值的方法：

1. **RAILWAY_PROJECT_ID**: Railway项目URL中的ID（全局唯一）
2. **RAILWAY_TOKEN**: 每个环境的专用token（在对应environment中配置）
3. **RAILWAY_SERVICE_ID**: 每个环境的服务ID（在对应environment中配置）
4. **SERVICE_BASE_URL**: 每个环境的访问URL（在对应environment中配置）

### Railway Token获取方法：
Railway为每个环境提供独立的token，你需要：
1. 在Railway Dashboard中切换到对应环境
2. 获取该环境专用的token
3. 在GitHub对应的Environment中配置

### 配置Environment Secrets的步骤：

1. 进入 `Settings` → `Environments`
2. 点击对应的环境名称（如 `uat` 或 `prod`）
3. 在 `Environment secrets` 部分点击 `Add secret`
4. 添加该环境特定的secrets

## 3. Railway环境配置

在Railway中为每个环境配置不同的环境变量：

### DEV环境变量:
```
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=your_dev_database_url
MYSQL_USERNAME=your_dev_db_username
MYSQL_PASSWORD=your_dev_db_password
```

### UAT环境变量:
```
SPRING_PROFILES_ACTIVE=uat
DATABASE_URL=your_uat_database_url
MYSQL_USERNAME=your_uat_db_username
MYSQL_PASSWORD=your_uat_db_password
```

### PROD环境变量:
```
SPRING_PROFILES_ACTIVE=prod  
DATABASE_URL=your_prod_database_url
MYSQL_USERNAME=your_prod_db_username
MYSQL_PASSWORD=your_prod_db_password
```

## 4. 部署流程操作指南

### 自动触发阶段：
1. 向release分支推送代码
2. GitHub Actions自动运行`test`和`sonar`作业
3. 完成后，workflow会暂停等待UAT部署批准

### 手动批准UAT部署：
1. 进入 `Actions` → 选择运行中的workflow
2. 点击 `deploy-uat` 作业
3. 点击 `Review deployments`
4. 选择 `uat` 环境
5. 可选择填写批准消息
6. 点击 `Approve and deploy`

### UAT测试通过后批准PROD部署：
1. UAT部署完成并测试通过后
2. 在同一个workflow中点击 `deploy-prod` 作业
3. 点击 `Review deployments` 
4. 选择 `prod` 环境
5. **仔细确认**后点击 `Approve and deploy`

## 5. 监控和回滚

### 健康检查：
- 每个环境部署后都会自动进行健康检查
- 检查端点：`/health`
- 失败会自动停止workflow

### 如果需要回滚：
1. 在Railway Dashboard中手动回滚到上一个版本
2. 或者推送修复代码到release分支重新部署

## 6. 最佳实践

### 部署时机：
- **UAT部署**: 功能开发完成，需要集成测试时
- **PROD部署**: UAT测试通过，业务确认可以上线时

### 审批人员：
- **UAT**: 开发团队Lead或QA负责人
- **PROD**: 项目经理 + 技术负责人（双重审批）

### 发布流程建议：
1. 在release分支完成最终测试
2. 批准UAT部署，进行集成测试
3. 测试通过后，召开上线评审会议
4. 获得业务和技术双重确认后批准PROD部署

## 7. 故障处理

### 如果部署失败：
1. 查看GitHub Actions日志
2. 检查Railway服务状态
3. 确认environment variables配置
4. 检查数据库连接

### 紧急回滚：
```bash
# 在Railway Dashboard中
1. 选择对应服务
2. 进入Deployments页面  
3. 选择上一个稳定版本
4. 点击Redeploy
```

这个配置确保了release分支的部署是可控的、安全的，同时保持了必要的灵活性。
