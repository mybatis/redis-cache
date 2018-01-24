/**
 *    Copyright 2015-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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

public class SimpleBeanCourseInfo implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  String name;
  int id;

  public SimpleBeanCourseInfo() {
    this.name = "Machine Learning";
    this.id = 101;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "CourseInfo [name=" + name + ", id=" + id + "]";
  }

  @Override
  public boolean equals(Object obj) {
    // TODO Auto-generated method stub
    if (obj == null)
      return false;
    if (!obj.getClass().equals(SimpleBeanCourseInfo.class))
      return false;
    return this.toString().equals(obj.toString());
  }

}
