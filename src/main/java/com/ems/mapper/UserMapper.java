package com.ems.mapper;

import com.ems.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user where uid=#{uid}")
    User selectUserByUid(int uid);
    @Select("select * from user where username=#{username}")
//  1.2.1引入查询缓冲和token验证后不再支援关联查询
//  RecordAPI中当用户记录少于15条时直接返回record的关联查询已被废弃

//    @Results({@Result(property="uid", column="uid"),
//            @Result(property="role", column="role"),
//            @Result(property="password", column="password"),
//            @Result(property="name", column="name"),
//            @Result(property="money", column="money"),
//            @Result(property="tel", column="tel"),
//            @Result(property="address", column="address"),
//            @Result(property="info", column="info"),
//            @Result(property="records", column="uid", many=@Many(select="com.ems.mapper.RecordMapper.selectRecordsByUid"))})
    User checkUsername(String username);
    @Select("select uid,role,username,name,money from user where name like concat('%',#{name},'%')")
    @Results({@Result(property="uid", column="uid"),
            @Result(property="role", column="role"),
            @Result(property="username", column="username"),
            @Result(property="name", column="name"),
            @Result(property="money", column="money")})
    List<User> selectUserByName(String name);
    @Select("select * from user where money<#{money} and role='user'")
    List<User> selectUserByMoney(double money);
    @Select("select uid,role,username,name,money from user")
    @Results({@Result(property="uid", column="uid"),
            @Result(property="role", column="role"),
            @Result(property="username", column="username"),
            @Result(property="name", column="name"),
            @Result(property="money", column="money")})
    List<User> selectUsers();
    @Insert("insert into user(role,username,password,name,money,tel,address,info) "
            + "values(#{role},#{username},#{password},#{name},#{money},#{tel},#{address},#{info})")
    int insertUser(User user);
    @Update("<script>"
            +   "update user"
            +   "<set>"
            +       "<if test=\"role != null\">role=#{role},</if>"
            +       "<if test=\"password != null\">password=#{password},</if>"
            +       "<if test=\"name != null\">name=#{name},</if>"
            +       "<if test=\"money != null\">money=#{money},</if>"
            +       "<if test=\"tel != null\">tel=#{tel},</if>"
            +       "<if test=\"address != null\">address=#{address},</if>"
            +       "<if test=\"info != null\">info=#{info},</if>"
            +   "</set>"
            +   "<where>"
            +       "uid=#{uid}"
            +   "</where>"
            + "</script>")
    int updateUser(User user);
    @Delete("delete from user where uid=#{uid}")
    int deleteUser(int uid);
}
