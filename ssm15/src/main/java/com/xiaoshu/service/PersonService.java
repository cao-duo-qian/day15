package com.xiaoshu.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.jms.Destination;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.CompanyMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonVo;

import redis.clients.jedis.Jedis;


@Service
public class PersonService {

	@Autowired
	PersonMapper personMapper;
	
	@Autowired
	CompanyMapper companyMapper;
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	Destination queueTextDestination;
	

	public List<Company> findAll(){
		return companyMapper.selectAll();
	}
	
	public Person findByName(String expressName){
		Person p = new Person();
		p.setExpressName(expressName);
		return personMapper.selectOne(p);
		
	}
	
	public PageInfo<PersonVo> findPage(PersonVo personVo , Integer pageNum , Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<PersonVo> list = personMapper.findList(personVo);
		return new PageInfo<>(list);
		
	}
	public void addPerson(Person person){
		person.setEntryTime(new Date());
		person.setCreateTime(new Date());
		personMapper.insert(person);
		
		jmsTemplate.convertAndSend(queueTextDestination,JSONObject.toJSONString(person));
		
		/*Jedis j = new Jedis("127.0.0.1",6379);
		j.set(person.getExpressName(), person.getSex());*/
		
	}
	
	public void updatePerson(Person person){
		personMapper.updateByPrimaryKeySelective(person);
	}
	public void deletePerson(Integer id){
		personMapper.deleteByPrimaryKey(id);
	}
	
	public List<PersonVo> findList(PersonVo personVo){
		return personMapper.findList(personVo);
	}
	
	public List<PersonVo> countPerson(){
		return personMapper.countPerson();
	}
	
	public void importPerson(MultipartFile personFile) throws InvalidFormatException, IOException{
		Workbook workbook = WorkbookFactory.create(personFile.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		for (int i = 0; i < lastRowNum; i++) {
			Row row = sheet.getRow(i+1);
			String expressName = row.getCell(0).toString();
			String sex = row.getCell(1).toString();
			String expressTrait = row.getCell(2).toString();
			Date entryTime = row.getCell(3).getDateCellValue();
			String cname = row.getCell(4).toString();
			
			Person p = new Person();
			
			p.setExpressName(expressName);
			p.setSex(sex);
			p.setExpressTrait(expressTrait);
			p.setEntryTime(entryTime);
			
			Company p2 = new Company();
			p2.setExpressName(cname);
			Company company = companyMapper.selectOne(p2);
			p.setExpressTypeId(company.getId());
			p.setCreateTime(new Date());
			personMapper.insert(p);
		}
	}
}
