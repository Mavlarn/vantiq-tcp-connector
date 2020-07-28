# vantiq-tcp-connector

tcp-connector for VANTIQ是通过tcp socket与目标系统集成的Connector。它有两种模式：
1. server模式：在这种模式下，Connector程序作为Socket的服务端，目标系统作为客户端连接Connector。
2. Client模式：在这种模式下，Connector程序作为客户端，通过TCP去连接目标系统。（目前还未实现）

通过Connector与目标系统建立Socket连接以后，Connector从Socket读取数据流，并按照协议解析成JSON数据，发送给VANTIQ。Connector也会接收VANTIQ的请求，将它转换成字节流，并通过Socket发送给目标系统。


## 使用
1. 在VANTIQ注册TCP数据源类型 
2. 在VANTIQ创建一个源
3. 启动Tcp Connector程序


### 注册TCP Connector数据源类型
注册数据源类型有两种方式：
1. 通过命令行程序注册新类型：   

创建一个配置文件：*tcpSource.json*:
```
{
   "name" : "TCPSource",
   "baseType" : "EXTENSION",
   "verticle" : "service:extensionSource",
   "config" : {}
}
```

通过VANTIQ的客户端程序运行:
```
vantiq -s <profileName> load sourceimpls tcpSource.json
```

2. 在系统表里添加类型记录   
在Show – Add Record 打开添加记录页面，勾选显示系统类型，找到往sourceimpl的系统类型，添加记录。添加的值如下：
```
   "name" : "TCPSource",
   "verticle" : "service:extensionSource",
```

### 创建数据源
在VANTIQ中，添加一个源，设置需要的名字，如*E7_Tcp_Source*，类型是 *TCPProtoSource*, 配置如下:
```
{
    "tcp_server_port": "localhost",
    "tcp_heart_beat": 30
}
```
`tcp_server_port`是作为TCP Server监听的端口。


### 运行Connector程序
如果还没有打包的程序，先进行打包
```
# package
mvn package -Dmaven.test.skip=true 
```
然后需要在当前的工作目录中，有一个配置文件，用于配置要连接到那个VANTIQ服务器，和Namespace。配置如下：
```json

{
    "vantiqUrl": "http://dev.vantiq.com",
    "token": "<your token for this source>",
    "sourceName": "<source name created in vantiq>"
}
```

然后运行：
```
# and run
java -jar target/tcp-connector-1.0-SNAPSHOT-jar-with-dependencies.jar
```
在日志中，看到运行的结果显示连接VANTIQ成功，并且连接目标服务器成功，及代表配置正常。

### 验证
如需验证Connector，可以在日志中查看heartbeat的日志，它会每隔一定时间往模板服务发送报文，并接收回复报文。

也可以在VANTIQ里面，发送报文给目标系统。可以在VANTIQ中创建一个Procedure，如下：
```javascript
PROCEDURE testSendDataToTCPConnector()

var packet = {
    head: "78B6",
    command: "4CE6",
    direction: "01",
    location: "03",
    dataLength: 104,
    data: {
        serialNo: "test_serialNo",
        projectNo: "5A9C5ED4-BFA2-4062-8532-2DBBC7109B85",
        location: "02",
        operationType: "01",
        comment: "test cmd"
    },
    checkSum: "14",
    "end": "21D3"
}

PUBLISH packet TO SOURCE E7TCPSource 
```
运行该Procedure，在Connector的日志中查看。这是一个手动开闸的指令，data里面的信息需要正确才能收到回复的报文。

## tcp协议
Tcp Connector与目标系统进行数据收发，需要对接收到的字节流的数据进行解析，转换成JSON类型的数据，而对于发送到目标系统的数据，又要从JSON格式转换为字节流。而TCP Connector的这个转换的过程，可以通过配置文件进行：

### tcp-packet.json
`tcp-packet.json`里面是收到的报文的格式，定义的是每个数据包的封装方式：
```json
[
    {
      "field": "head","fixCode": "78B6","length": 2,"type": "byte"
    },
    {
      "field": "command","fixCode": "","length": 2,"type": "byte"
    },
    {
      "field": "direction","fixCode": "","length": 1,"type": "byte"
    },
    {
      "field": "location","fixCode": "","length": 1,"type": "byte"
    },
    {
      "field": "dataLength","fixCode": "","length": 2,"type": "integer"
    },
    {
      "field": "data","fixCode": "","length": "0","type": "data"
    },
    {
      "field": "checkSum","fixCode": "","length": 1,"type": "byte"
    },
    {
      "field": "end","fixCode": "21D3","length": 2,"type": "byte"
    }
  ]
```
可以看到，在这个例子中，每个包都有固定的包头包尾，有一个变长的`data`数据，还有相应的`checksum`的校验值。Connector中的`PacketConvertor`会根据这个文件的定义进行转换。

### tcp-command.json
对于data数据的部分，根据`tcp-command.json`文件里面的配置进行解析：
```json
{
  "4C5A": {
    "command": "4C5A",
    "commandName": "HeartBeat",
    "request": {
      "length": 10,
      "fields": [
        {"field": "controllerNo", "length": 6, "type": "string"},
        {"field": "heartBeatSeq", "length": 4, "type": "integer"}
      ]
    },
    "response": {
      "length": 11,
      "fields": [
        {"field": "controllerNo", "length": 6, "type": "string"},
        {"field": "heartBeatSeq", "length": 4, "type": "string"},
        {"field": "answer", "length": 1, "type": "byte", "match": "59"}
      ]
    }
  },
  "4CE6": {
    "command": "4CE6",
    "commandName": "OpenGate",
    "request": {
      "length": 104,
      "fields": [
        {"field": "serialNo", "length": 36, "type": "string"},
        {"field": "projectNo", "length": 36, "type": "string"},
        {"field": "location", "length": 1, "type": "byte"},
        {"field": "operationType", "length": 1, "type": "byte"},
        {"field": "comment", "length": 30, "type": "string"}
      ]
    },
    "response": {
      "length": 37,
      "fields": [
        {"field": "serialNo", "length": 36, "type": "string"},
        {"field": "answer", "length": 1, "type": "byte", "match": "59"}
      ]
    }
  }
}
```

### 转换规则
除了根据上面的规则进行转换，还有一些固定的规则，如：
1. 所有的数字都是以字符串类型进行字节转换。长度不够时，数字前面补"0"。
2. 所有的command都是`hex string`类型的字符串。