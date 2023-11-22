/*
 *    Copyright 2015-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.caches.redis;

import java.io.Serializable;
import java.util.ArrayList;

public class SimpleBeanStudentInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  String name;
  int age;
  int grade;
  String sex;
  ArrayList<String> courses;

  public SimpleBeanStudentInfo() {
    this.name = "Kobe Bryant";
    this.age = 40;
    this.grade = 12;
    this.sex = "MALE";
    this.courses = new ArrayList<String>();
    this.courses.add("English");
    this.courses.add("Math");
    this.courses.add("Physics");
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getGrade() {
    return grade;
  }

  public void setGrade(int grade) {
    this.grade = grade;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  @Override
  public String toString() {
    return "StudentInfo [name=" + name + ", age=" + age + ", grade=" + grade + ", sex=" + sex + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (!obj.getClass().equals(SimpleBeanStudentInfo.class))
      return false;
    return this.toString().equals(obj.toString());
  }

}
