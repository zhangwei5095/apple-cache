package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;

public class CacheCommandReplicator {
	
	private static Logger logger = Logger.getLogger(CacheCommandReplicator.class);
	
	private String name = "J2_CACHE_MANAGER";

	private JChannel channel;	
	
	public void setName(String name) {
		this.name = name;
	}

	public void init() {
		try {
			channel = new JChannel();
			channel.connect(name);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void destroy() {
		channel.close();
	}
	
	public void replicate(Command command) {
		try {
			/**
			 * 参数里指定Channel使用的协议栈，如果是空的，则使用默认的协议栈，
			 * 位于JGroups包里的udp.xml。参数可以是一个以冒号分隔的字符串， 或是一个XML文件，在XML文件里定义协议栈。
			 */
			logger.warn("发送同步数据");
			// 创建一个通道
			
			// 发送事件
			// 这里的Message的第一个参数是发送端地址
			// 第二个是接收端地址
			// 第三个是发送的字符串
			// 具体参见jgroup send API
			Message msg = new Message(null, null, command);
			// 发送
			channel.send(msg);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
