#NodeMediaClient Android Demo
##简介
NodeMediaClient是为移动端应用量身打造的基于RTMP协议的流媒体直播系统。通过集成本SDK，只需几个简单API调用，便可实现一套完整的直播流媒体应用基础。包含了流媒体应用中：『采集->编码->传输->解码->播放』的所有步骤。
##支持的平台
Android 2.3及以上
##支持的CPU架构
armeabi-v7a arm64-v8a x86
##支持的流媒体服务端
fms, wowza, evostream, red5, crtmpserver, nginx-rtmp-module, srs, Node-Media-Server
##支持的流媒体云服务
奥点云，七牛云
##直播发布特性
* H.264/AAC 组合的RTMP协议音视频流发布
* 全屏视频采集，720p原画质缩放
* NEON/SSE指令集优化H.264软件编码器，性能强劲，兼容性极强
* H.264支持Baseline及Main profile
* 视频支持横屏16:9，竖屏9:16分辨率自动原画旋转
* NEON优化AAC软件编码器，极少CPU占用，支持LC和HE profile，音质还原效果好
* 支持环境背景噪音抑制，不再有沙沙声
* 支持发布中途切换前后摄像头
* 支持闪光灯开关
* 支持全时自动对焦

##直播播放特性
* 只为RTMP协议优化的码流解析器，极短的分析时间，秒开RTMP视频流
* NEON/SSE指令集优化的软件解码器，性能好，兼容性强
* 支持的视频解码器:H.264, FLV, VP6
* 支持的音频解码器:AAC, MP3, SPEEX, NELLYMOSER, ADPCM_SWF, G.711
* OpenGL ES视频渲染
