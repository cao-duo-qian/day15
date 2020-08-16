package com.xiaoshu.dao;

import java.util.List;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonVo;

public interface PersonMapper extends BaseMapper<Person> {
	public List<PersonVo> findList(PersonVo personVo);
	
	public List<PersonVo> countPerson();
}