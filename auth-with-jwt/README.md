auth-with-jwt
-------------------

## 测试步骤

- 启动认证服务器`JwtAuthServerApplication`

- 启动资源服务器`JwtResourceServerApplication`

- 从认证服务器获取token

```
curl -XPOST "web_app:@localhost:9999/oauth/token" -d "grant_type=password&username=reader&password=reader"
```

记录下获取到的token。

- 访问资源服务器

```
export TOKEN=eyJhbGciOiJSUzI
curl -H "Authorization: Bearer $TOKEN" "localhost:9998/"
```
