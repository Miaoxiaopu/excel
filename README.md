# excel
excel上传下载，文件内容以字节保存在数据库，文件使用minio保存到磁盘


如下命令运行jar包
java -Xms1024m -Xmx1024m -Xmn250m -Xss256k -XX:+UseConcMarkSweepGC -XX:ParallelGCThreads=20 -server -jar gov_data-1.0-SNAPSHOT.jar
