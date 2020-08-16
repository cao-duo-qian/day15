package com.xiaoshu.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Person;

import redis.clients.jedis.Jedis;




public class MyMessageListener implements MessageListener{
	
	@Autowired
	private PersonMapper personMapper;
	
	
	@Override
	public void onMessage(Message message) {
		//接收消息  发送json字符串 接受需要强转成String
		TextMessage msg = (TextMessage)message;
		
		//获取消息
		try {
			String json = msg.getText();
			System.out.println(json);
			
			Person person = JSONObject.parseObject(json, Person.class);
			Jedis j = new Jedis("127.0.0.1",6379);
			Person p = new Person();
			p.setExpressName(person.getExpressName());
			Person person2 = personMapper.selectOne(p);
			j.set(person.getExpressName(), person2.getId()+"");

		
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
