## gateway方法的cache

### 机制
 对controller进行AOP，实现HTTP Restful接口的cache。 请求的处理流程：
  - 从一级cache中找，看是否hit,如果hit，直接返回结果.
  - 如果一级cache miss，则调用服务.
     - 如果服务调用成功，则将服务调用结果返回，并且设置到一级cache和二级cache中.
     - 如果服务调用失败，则从二级cache中取结果返回.

### 配置
 在`config.properties`中添加配置项：
 ```bash

 # level 1 cache address and expire in seconds (default is 30 min)
 level1.cache.address=1.2.3.4:23
 level1.cache.expire=1800

  # level 2 cache address and expire in seconds (default is 12 hour)
  level2.cache.address=1.2.3.4:23
  level2.cache.expire=43200

 ```

### 如何使用？

1.在`controller`中要cache的方法中添加如下`@Cachable`标签，例如：
```java

 @RequestMapping(params = "method=app.passport.signin")
    @Cachable
    public
    @ResponseBody
    ApiResponse signin(@RequestParam("password") String password,
                        @RequestParam("profile") String profile) throws com.yoho.gateway.exception.GatewayException {
}
```

2.如果`controller`方法的参数是`POJO`，则需要实现`com.yoho.gateway.cache.CacheKey`，反之，如果`controller`方法的参数是基本类型，会自动生成cache key。

### Cache key的生成机制
cache key的前缀为：`yh_gw:signin:`,其中，`siginin`是方法的名称。
 - 方法的参数用`@RequestParam` 表示，都是基本类型，则会将参数用`-`连接起来作为key，例如，上述函数，如果`password=12, profile=qqq`，则生成的
   key为：  `yh_gw:signin:12-qqqq`
 - 方法的参数是`POJO`,则需要实现`com.yoho.gateway.cache.CacheKey`接口，返回`key`，框架会自动加上前缀：  `yh_gw:signin:`

### 如果查询cache命中率？

通过jmx查询，`objectName` 为 `yh:system=gateway`， 需要传入方法的名称，例如`signin`。

