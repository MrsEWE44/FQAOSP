
# FQAOSP是一个适用于类原生的搞机工具，同样也适用于国内定制ui系统，它拥有很多常用的功能.

例如：后台清理、一键卸载与安装应用、安装某个指定的文件夹里面所有apk/apks文件、将手机本地的系统镜像文件挂载给电脑用，可以给电脑重装
系统、反/回编译本地软件、提取或者刷入系统分区文件、软件的备份与恢复(支持分身空间的软件备份与恢复)、应用分身(默认最高支持开启1024个分身)、共享手机
本地文件给局域网内所有用户、搜索自己设定范围内的文件、应用权限管控、应用联网管控、设置ntp服务器、同步北京时间等等.

未来还会加入更多功能，现在部分功能已经可以不再需要root，已经对接了adb授权，但是仍有部分需要root才能使用，后续会逐渐完善与adb的对接。

如果有新功能或建议，可以在GitHub提issue！

当前软件最新版本为：V1.3.7b

# V1.3.7b

1.修复软件更新时出现的错误

2.优化appos命令参数拼接

3.修改版本号为1.3.7b(小修复)


# V1.3.7a

1.修复数据库编辑功能.

2.加入iptable数据库编辑功能.

3.修改软件说明.

4.修改版本号为V1.3.7a.


# V1.3.7

1.修复应用管理功能内无法配置应用权限问题.

2.修复文件共享闪退问题.

3.加入网络规则数据库,在切换至联网控制的时候,会自动查询数据库并生效之前配置的规则属性.需要手动启动.

4.修改版本号为V1.3.7.


# V1.3.6t

1.修复安卓13应用恢复后，打开无法读取之前备份的数据，或者闪退问题.

2.优化弹窗进度显示.

3.优化获取组件、应用列表、扫描本地文件的界面交互.

4.优化应用内存占用.

5.修改版本号为V1.3.6t.


# V1.3.6

1.修复应用组件搜索闪退问题.

2.增加弹窗进度显示.

3.增加备份/恢复应用分身功能.

4.修改版本号为V1.3.6.


# V1.3.5

1.修复adb授权时候.出现路径错误的问题.

2.修复adb授权后,部分功能会使用失败的问题.

3.修复安卓4.4设备内部存储路径获取问题(实验性).

4.修改版本号为V1.3.5.

# V1.3.4

1.加入adb授权支持,可以通过adb shell来授权该应用,来运行一些需要adb权限才能做到的事情.

2.加入安卓4.4支持,安卓4.4设备也可以使用该应用了.

3.移除shizuku依赖.

4.修改版本号为V1.3.4.


# V1.3.3

1.加入屏幕分辨率修改功能.

2.修复anr弹窗无响应问题.

3.修改版本号为1.3.3


# V1.3.2

1.加入ntp服务器、状态栏去X标志功能.

2.合并应用管理里面得联网控制至权限列表选项里面.需要root权限使用.

3.修改版本号为V1.3.2


# V1.3.1

1.加入小工具集合(同步设置国内标准时间/设置设备刷新率/开启墓碑模式).

2.在应用管理界面加入应用待机与强制应用打盹配置选项.

3.修改版本号为V1.3.1


# V1.3.0

1.合并payload/img2sdat/sdat2img源码.

2.修复分区提取与刷入功能.

3.更改makefqtools与rom工具功能参数位置

4.修改版本号为V1.3.0


# V1.2.9

1.完善手机分身功能,修复旧bug.

2.手机分身功能对接shizuku权限,不需要root也可以实现分身.

3.删除workProfileDB数据库功能.

4.修改版本号为V1.2.9


# V1.2.8

1.修复重复弹窗问题.

2.修复安装本地apk文件出现空指针问题.

3.添加arm的busybox.

4.修改版本号为V1.2.8


# V1.2.7

1.添加任务结束后的弹窗提示,错误提示以及错误信息.

2.修改备份与恢复功能部分,需要强制安装fqtools才能使用的问题.

3.在备份与恢复功能中,新增tar.br压缩格式,lzma的压缩效率,gzip的解压速度.

4.修复后台管理不能正确读取应用内存占用的问题.

5.修改版本号为V1.2.7


# V1.2.6

1.添加rom工具打包镜像文件为sparse功能.

2.在应用管理界面,添加列出应用广播接收器功能.

3.修复备份还原时无法正确运行的问题.

4.修复makefqtools脚本无法正确构建的问题.

5.修改版本号为V1.2.6


# V1.2.5

1.添加rom镜像打包功能.

2.新增rom工具功能，合并rom解包、打包功能至rom工具界面.

3.删除无用函数与文件

4.修改版本号为V1.2.5


# V1.2.4-rombuild

1.添加rom工具选项，加入rom解包功能实现。

2.耦合fqtools工具包，精简无用内容，缩减体积，更改压缩方式。

3.剔除禁用应用、镜像工具功能，禁用应用功能合并至应用管理当中。

4.应用管理界面添加降级、调试、覆盖安装、应用禁用、启用功能选项。

5.新增makefqtools脚本，用户可以在termux里面手动制作最新的fqtools工具包，无需手动下载或者等待更新。

6.更改版本号为V1.2.4-rombuild


# V1.2.3

1.添加应用启动图标。

2.添加侧边菜单选项颜色，用于区分是否拥有权限使用该功能。

3.修改版本号为1.2.3


# v1.2.2

1.软件反编译/回编译功能不再需要root权限即可正常使用。

2.工具导入界面不再需要root亦可正常导入。

3.剔除jni部分无用功能，采用纯Java开发(减少体积)。

4.降级targetsdk版本号为28(Android Q)，使其可以正常调用自带命令文件。

5.修改版本号为1.2.2


# V1.2.1

1.在《应用管理》功能界面中加入权限管控功能，对接shizuku，可以批量生效所选择的权限。

2.修改版本号为1.2.1


# V1.2.0

1.修复文件共享时，局域网内其它人可以直接访问机主所有文件内容bug，剔除/android/data与obb的访问权限，不再列出Android文件夹以及里边所有内容。

2.应用管理、后台管理、禁用app大部分功能已经对接shizuku，只需要通过shizuku激活授权即可正常使用。

3.对应用管理里面的《选择本地安装包文件夹》做出优化，对接shizuku，不再需要root权限以及安装fqtools工具包。

4.在工具检查页面加入《在线下载》功能，不再需要自己再去网站上找工具包下载，下载完后，会自动安装(需要root权限)。

5.提升最低sdk版本为24(安卓7.x)，对接shizuku授权。

6.修改版本号为1.2.0


# V1.1.9

1.更改主页界面布局

2.添加应用更新检测与下载

3.分离软件备份与恢复页面，清晰操作步骤与流程

4.新增分区管理，可以提取与刷入手机分区文件

5.降低最低sdk版本为21(安卓5.x)，缩减程序体积

6.修改版本号为1.1.9


# V1.1.8a

1.修复一键安装应用结束后无法关闭弹窗问题

2.优化部分功能区代码

3.提升最低安装sdk版本为28(安卓9)

4.修改版本号为1.1.8a


# V1.1.8

1.修复文件共享网络IP获取问题

2.初次重构应用分身、镜像工具、apk反编译功能区布局

3.修改jdk为64位，32位只在旧版本提供

4.修改版本号为1.1.8


# V1.1.7a

1.新增局域网文件夹共享功能,支持/android/data与obb访问、下载

2.修改版本号为1.1.7a


# V1.1.7

1.新增局域网文件共享功能，只需要一个人安装此客户端，开启文件共享，其它人不需要安装客户端，即可下载、预览分享的内容。

2.修改版本号为1.1.7


# V1.1.6

1.在U盘功能区里面，新增镜像文件创建功能，支持格式化为指定分区格式，支持文件大小单位设置。

2.优化U盘挂载功能，部分机型系统可支持mtp与U盘两种模式共存。

3.在应用管理功能区里面加入一键安装文件夹里面所有apk/apks文件功能。

4.删减、优化部分脚本。

5.修改版本号为1.1.6


# V1.1.5a

1.修改部分文字说明与功能名称

2.修复文件搜索必须要点击选择路径才能弹出权限申请问题

3.修改版本号为1.1.5a


# V1.1.5

1.修复appopsinfo无法正常禁用组件问题

2.新增文件搜索功能

3.新增各个功能模块帮助说明选项以及功能介绍

4.新增应用备份里的详细备份、备份类型两个选项

5.修改版本号为1.1.5


# V1.1.4a

1.修复调用命令时会出现部分语法错误问题

2.修改extract脚本

3.修改版本号为1.1.4a


# V1.1.4

1.更改备份脚本压缩为tar.xz

2.修复应用分身功能不稳定问题，采用handler代替线程池

3.完善libterm库功能

4.修復若干bug

5.修改版本号为1.1.4


# V1.1.3

1.修復備份脚本無法正確備份、還原問題

2.修復應用分身不穩定功能，移除綫程池采用handler代替

3.添加libterm庫，添加jni調用命令功能

4.修復若干bug

5.修改版本号为1.1.3


# V1.1.2

1.首页新增应用恢复与备份选项

2.添加应用恢复与备份功能脚本

3.添加apks文件安装脚本

4.支持apks/split apk类型应用备份、恢复

5.fqtools.tar新增bar脚本，支持apks文件安装、应用备份与恢复

6.优化分身管理效率，修复线程问题

7.修改版本号为1.1.2


# V1.1.1

1.应用管理界面长按功能新增：应用安装功能

2.应用管理界面新增选择应用功能，选择本地应用可长按复制apk信息

3.新增应用版本号、大小显示

4.新增后台各个应用内存占用显示

5.优化后台清理效率

6.分离multiFunc工具类，新增filetools用于处理文件操作

7.删除小组件

8.修改版本号为1.1.1

9.更改签名文件

10.新增apks文件

# V1.1.0

1.移除应用提取功能界面,应用提取功能附加到应用管理功能长按界面

2.应用管理界面长按功能新增：应用卸载、提取功能

3.修复分身应用查询时出现主线程卡死问题

4.修改版本号为1.1.0


# V1.0.9

1.添加应用管理部分,长按导出已勾选应用包名功能

2.修改版本号为1.0.9


# V1.0.8

1.添加app包名显示

2.完善搜索功能，新增包名搜索

3.添加应用信息复制

4.修改版本号为1.0.8



# V1.0.7

1.修复appopsinfo修改部分组件失败

2.剔除小部件功能，但保留小部件选项

3.完善搜索功能，不区分大小写

4.修改appopsinfo组件排序方式

5.修改版本号为1.0.7


# V1.0.6

1.应用权限、服务、活动项更改操作方式，简化操作

2.更改后台管理操作方式，简化操作

3.在上述功能页面新增搜索框,提供搜索功能

4.强化部分功能

5.修改版本号为1.0.6



# V1.0.5

1.优化应用管理(appops)网络管理功能并简化界面

2.修复listview滑动时，checkbox选中界面变回未选中问题

3.优化效率



# V1.0.4

1.添加应用分身数据库

2.修复新增应用分身时，相关应用默认同步到所有用户问题

3.sql管理新增分身数据库增删改查

4.改为release签名版本


# V1.0.3

1.添加killapp勾选列表存入数据库，方便下次默认调用

2.添加killapp数据库增删改查功能


# V1.0.2

1.新增分身应用管理功能

2.强化appops

3.添加各个功能组件窗口标题

4.添加应用搜索功能

5.修复导入外部文件时大小显示错误问题

6.更改版本号为1.0.2



# V1.0.1

1.缓解分身功能卡死、闪退、无响应问题

2.添加相关提示框

3.在主页添加跳转工具包检查页面

4.优化部分效率问题

2022年4月10日11点25分


# V1.0.0

FQAOSP

1、支持已安装应用导出、组件禁用/启用、权限授权/撤销、活动项禁用/启用

2、支持将手机上的镜像文件以U盘模式挂载到电脑上，能让电脑识别

3、支持工具包外部导入，减少体积占用

4、支持apk反编译、回编译

5、支持boot、recovery解包/打包

6、支持应用分身，默认限制1024个

7、后台管理，一键清理、选中部分、未选中部分

8、支持已安装应用禁用、启用，添加主流深度定制ui禁用app策略，一键禁用


后续待添加。。。。

2022年4月9日18点07分


# V1.0.0-BETA

添加应用组件禁用与启用功能，可修改应用服务、权限、活动项，实现写轮眼功能。

添加应用禁用与启用，并添加主流ui禁用策略。

移除内置jdk，采用外部导入。

