# mock-method-sample
* 使用场景
    * 调试阶段，非调试的逻辑影响了我们调试逻辑，我们使用注解@MockMethod方法轻松解决
        * 四宫格和六宫格切换
        * vip和非vip的场景切换

* 安装
    * 将mock-method-plugin 上传到本地仓库
        * 选择右侧Gradle -> :mock-method-plugin -> upload -> uploadArchives
    * 将mock-mehtod-annotation 上传到本地仓库
        * 方法同上
    <img src="screenshot/ic_upload_jar.jpeg" width="200" height="400" alt="avatar" />

    * 运行

* Mock 截图
    * java代码
    <img src="screenshot/ic_mock_source.png" width="400" height="90" alt="avatar" />

    * 编译后的代码
    <img src="screenshot/ic_mock_class.png" width="500" height="100" alt="avatar" />