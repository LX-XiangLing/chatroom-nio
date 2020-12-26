基于NIO开发的简单聊天室

启动步骤:在test包下先启动ServerStart服务器端 再启动Client端

最近在学习NIO的相关知识 觉得光看学不会 就试着写了一个简单的聊天室 

NIO的三大核心组件
Channel(通道):
Java NIO中的所有I/O操作都基于Channel对象
Channel使用起来跟Stream比较像，可以读取数据到Buffer中，也可以把Buffer中的数据写入
Stream是单向的,分InputStream和OutputStream 而Channel是双向的，既可用来进行读操作，又可用来进行写操作
Channel有非阻塞I/O模式         Channel.configureBlocking(false);设置Channel为非阻塞

常见的Channel:
*FileChannel：读写文件
*DatagramChannel: UDP协议网络通信
*SocketChannel：TCP协议网络通信
*ServerSocketChannel：监听TCP连接

Buffer(缓存区):
NIO中所使用的缓冲区不是一个简单的byte数组,而是封装过的Buffer类,通过它提供的API,我们可以灵活的操纵数据
与Java基本类型相对应,NIO提供了多种Buffer类型,如ByteBuffer、CharBuffer、IntBuffer等

重要变量:
*capacity(总容量)  *position(指针当前位置)  *limit(读/写边界位置)
在对Buffer进行读/写的过程中,position会往后移动,而 limit 就是 position 移动的边界。
由此不难想象,在对Buffer进行写入操作时,limit应当设置为capacity的大小，
而对Buffer进行读取操作时,limit应当设置为数据的实际结束位置。
(注意:将Buffer数据写入通道是Buffer读取操作,从通道读取数据到Buffer是Buffer写入操作)

写入数据到Buffer
调用flip()方法
从Buffer中读取数据调用
clear()方法或者compact()方法

常用API:
*flip(): 设置limit为position的值,然后position置为0 对Buffer进行读取操作前调用
*rewind(): 仅仅将position置0 一般是在重新读取Buffer数据前调用,比如要读取同一个Buffer的数据写入多个通道时会用到 
*clear(): 回到初始状态,即limit等于capacity,position置0 重新对Buffer进行写入操作前调用
*compact(): 将未读取完的数据(position与limit之间的数据)移动到缓冲区开头,并将position设置为这段数据末尾的下一个位置 其实就等价于重新向缓冲区中写入了这么一段数据

Selector(选择器):
类似gateway，进行对channel的注册与发现
Selector是一个特殊的组件,用于采集各个通道的状态(或者说事件)我们先将通道注册到选择器,并设置好关心的事件,然后就可以通过调用select()方法,静静地等待事件发生
Selector是NIO的核心，是select/epoll/poll的外包类，实现了IO多路复用的关键

*OP_ACCEPT —— 接收连接继续事件 表示服务器监听到了客户连接，服务器可以接收这个连接了
*OP_CONNECT —— 连接就绪事件 表示客户与服务器的连接已经建立成功
*OP_READ —— 读就绪事件 表示通道中已经有了可读的数据，可以执行读操作了（通道目前有数据，可以进行读操作了）
*OP_WRITE —— 写就绪事件 表示已经可以向通道写数据了（通道目前可以用于写操作）

NIO擅长1个线程管理多条连接,节约系统资源,但是如果每条连接要传输的数据量很大的话,因为是同步I/O,会导致整体的响应速度很慢；
而传统I/O为每一条连接创建一个线程,能充分利用处理器并行处理的能力,但是如果连接数量太多,内存资源会很紧张。
总结就是：连接数多数据量小用NIO,连接数少用I/O


================================部分内容收集自各个博客 如有侵权联系删除================================