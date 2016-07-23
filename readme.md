

### web.xml中的配置
#### `spring active profile` 配置

```xml
     <context-param>
        <param-name>spring.profiles.active</param-name>
        <param-value>local,production</param-value>
    </context-param>
```
配置说明: 
- 服务调用方式, 范围: (`local`, `zookeeper`), 影响: 1.服务调用的客户端和服务端基本本地文件还是`zookeeper`
- 生产环境还是测试环节:  范围: (`production`, `test` ), 影响： 未启用
 
 
#### 如何不校验client_security?

 - 配置gateway目录下 `WEB-INF\classes\config.properties`中的`is_debug_enable=true`. 然后重启
