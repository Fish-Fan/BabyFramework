### 读取xml文件的过程
1. 读取所有的bean,并且设置bean的属性，如果碰到属性的类型为内联bean，
那么创建这个内联bean的实例，如果读取到属性的类型为ref(引用其他bean的实例)，那么创建
一个Ref对象，保存该引用的信息。最后将所有的bean保存至beanContainer<String,Bean>
容器中，其中String为bean的ID,Bean为保存xml信息的对象。
2. 通过Bean对象中定义的信息，创建bean的实例
3. 如果容器中存在ref(引用其他bean的实例)，那么从beanContainer容器中获取该实例，并保存至
Bean对象中的property属性中。
4. 刷新beanContainer容器，即将ref类型的bean通过反射设置为bean实例的属性。
### xml文件书写规范
xml文件书写与xml标准一致，目前支持bean加载，bean之间的ref引用。
对bean属性类型的支持如下表所示:

| 基本类型          |
|-------------------|
| int (Integer)     |
| long (Long)       |
| float (Float)     |
| double (Double)   |
| boolean (Boolean) |
| String            |
| **集合类型**          |
| List              |
| Set               |
| Map               |


+ 让容器加载bean

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans>
  <!--id可写可不写，但是如果需要根据id获取该bean是必须给这个bean一个唯一的id-->
  <!--class路径为相对应用根目录开始的地址-->
  <bean id="JacksonUserCard" class="babyframeworktest.pojo.UserCard" />
</beans>
```

+ 设置bean的属性

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans>
  <bean id="JacksonUserCard" class="babyframeworktest.pojo.UserCard">
      <!--支持内联bean,如果这个bean只使用一次，那么可以使用该方式-->
      <property name="card">
          <bean id="card" class="babyframeworktest.pojo.Card">
              <property name="material" value="NASA钛合金"/>
          </bean>
      </property>
      <!--value会根据bean中定义的类型自动转换，目前只支持上述类型的转换-->
      <property name="id" value="10000000001"/>
      <property name="gov" value="xxx"/>
  </bean>
</beans>
```

+ 引用其他bean

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans>
  <bean id="user1" class="babyframeworktest.pojo.User">
          <property name="username" value="Jackson"/>
          <property name="age" value="29"/>
          <!--ref的值为其他bean的id-->
          <property name="userCard" ref="JacksonUserCard"/>
  </bean>

  <bean id="JacksonUserCard" class="babyframeworktest.pojo.UserCard">
    <property name="id" value="10000000001"/>
    <property name="gov" value="xxx"/>
  </bean>
</beans>

```

+ 使用list
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans>
  <bean id="user1" class="babyframeworktest.pojo.User">
        <property name="userCard">
            <list>
                <!--list支持ref-->
                <value ref="JacksonUserCard"/>
                <!--没有ref的写法-->
                <value>xxx</value>
            </list>
        </property>
  </bean>
</beans>
```

+ 使用set
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans>
  <bean id="user1" class="babyframeworktest.pojo.User">
        <property name="userCard">
            <set>
              <!--set支持ref-->
                <value ref="JacksonUserCard"/>
                <!--没有ref的写法-->
                <value>xxx</value>
            </set>
        </property>
  </bean>
</beans>
```

+ 使用map
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans>
  <bean id="user" class="babyframeworktest.pojo.User">
        <property name="userCard">
          <!--map的key和value都支持ref，写法如下所示-->
            <map>
                <entry>
                    <key>Jackson</key>
                    <value ref="JacksonUserCard"/>
                </entry>
                <entry>
                    <key ref="personNameBean">Jackson1</key>
                    <value>persionNameBeanValue</value>
                </entry>
                <entry>
                    <key ref="personNameBean"/>
                    <value ref="JacksonUserCard"/>
                </entry>
                <!--支持内联bean-->
                <entry>
                    <key>
                        <bean class="xxx"/>
                    </key>
                    <value>
                        <bean class="xxx"/>
                    </value>
                </entry>
            </map>
        </property>
    </bean>
</beans>
```

+ 设置bean的作用域

默认bean的作用域都为单例模式(singleton)，即容器中一个class对应一个bean,当需要获取到这个bean时，容器返回这个bean.
babyFramework支持另外一个作用域prototype,在该模式下，每次容易返回一个类的实例时，都会去创建一个新的bean,
即两个bean的hashCode是不相同的。

```xml
<beans>
    <!--通常可省略不写-->
    <bean id="xxx" scope="singleton"/>
    <!--设置bean的作用域为prototype,每次返回一个新的实例-->
    <bean id="xxxx" scope="prototype"/>
</beans>
```
