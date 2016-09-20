package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.core.replicator.CommandProcesser;
import com.appleframework.cache.j2cache.utils.SerializeUtility;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import redis.clients.jedis.Jedis;

public class CommandMulticastProcesser implements CommandProcesser {
	
	protected final static Logger logger = Logger.getLogger(CommandMulticastProcesser.class);
	
	private String name = "J2_CACHE_MANAGER";
		
	private CacheManager ehcacheManager;
	
	private CodisResourcePool codisResourcePool;
	
	@Override
	public void onProcess(Command command) {
		Object key = command.getKey();
		if(command.getType().equals(CommandType.CLEAR)) {
			this.getEhCache().removeAll();
		}
		else if(command.getType().equals(CommandType.PUT)) {
			this.getEhCache().remove(key);
			Integer timeout = command.getTimeout();
			try (Jedis jedis = codisResourcePool.getResource()) {
				byte[] rvalue = jedis.get(key.toString().getBytes());
				Object value = SerializeUtility.unserialize(rvalue);
				if(timeout == 0) {
					getEhCache().put(new Element(key, value));
				} else {
					getEhCache().put(new Element(key, value, timeout, timeout));
				}
			}
		}
		else if(command.getType().equals(CommandType.DELETE)) {
			this.getEhCache().remove(key);
		}
		else {
			logger.warn(command.getType().name());
		}
	}


	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}
	
	public Cache getEhCache() {
		Cache cache = ehcacheManager.getCache(name);
		if (null == cache) {
			ehcacheManager.addCache(name);
			return ehcacheManager.getCache(name);
		} else {
			return cache;
		}
	}
	
	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}
	
}
