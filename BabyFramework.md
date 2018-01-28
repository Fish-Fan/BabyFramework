### xml文件书写规范
xml文件书写与xml标准一致，目前支持bean加载，bean之间的ref引用。
对bean属性类型的支持如下表所示:
| 基本类型|
|----------|
| int (Integer)      |
| long (Long)     |
| float (Float)    |
| double (Double)   |
| boolean (Boolean)  |
| String   |  
| 集合类型 |
| List     |     
| Set      |    
| Map      |  
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
          <property name="userCard" ref="JacksonUserCard">
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
                    <key>Jackson1</key>
                    <value ref="JacksonUserCard"/>
                </entry>
                <entry>
                    <key>Jackson2</key>
                    <value ref="JacksonUserCard"/>
                </entry>
            </map>
        </property>
    </bean>
</beans>
```
