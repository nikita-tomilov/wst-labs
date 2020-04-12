## Prerequisites:
- docker, docker-compose, psql installed
 
## How-to:
- YOU ABSOLUTELY HAVE TO mvn clean package OTHERWISE IDEA WILL BE USELESS
- ./run_postgre_only.sh
- run juddi
- run standalone/tests/AppLauncher.java
- run client/client.java
- use as described in usage example
    
## jUDDI setup & troubleshooting
    wget http://archive.apache.org/dist/juddi/juddi/3.3.7/juddi-distro-3.3.7.zip
    unzip juddi-distro-3.3.7.zip
    cd juddi-distro-3.3.7.zip
    juddi-tomcat-3.3.7/bin/startup.sh 

if you can see in netstat -tlnup that there is noone listening to 8080, do the following:
- check juddi-tomcat-3.3.7/logs/juddi.log if it has "Failed to read external DTD 'XMLSchema.dtd'"
- sudo netstat -tlnup, find who is listening to ports 8005 and 8009
- kill it
- in another terminal open tail -f juddi-tomcat-3.3.7/logs/juddi.log
- relaunch juddi via:


    JAVA_OPTS=-Djavax.xml.accessExternalDTD=all juddi-tomcat-3.3.7/bin/startup.sh
 
- in tail -f wait until "jUDDI registry started successfully."
- http://localhost:8080/juddi-gui/ should now give you juddy


## Usage example

Connected to the target VM, address: '127.0.0.1:51659', transport: 'socket'

    Enter JUDDI username (juddi-tomcat-3.3.7/conf/tomcat-users.xml)
    uddiadmin
    Enter JUDDI user password
    da_password1
    Apr 12, 2020 12:45:27 PM org.apache.juddi.v3.client.config.UDDIClient <init>
    INFO: jUDDI Client version - 3.3.7
    Apr 12, 2020 12:45:27 PM org.apache.juddi.v3.client.config.ClientConfig loadConfiguration
    INFO: Reading UDDI Client properties file file:///home/hotaro/IdeaProjects/wst-labs/wst-lab-7/client/target/classes/META-INF/uddi.xml use -Duddi.client.xml to override
    Apr 12, 2020 12:45:27 PM org.apache.juddi.v3.client.transport.JAXWSTransport getUDDISecurityService
    WARNING: You should consider using a secure protocol (https) when sending your password!
    
    Выберите один из пунктов:
    0. Вывести help
    1. Список бизнесов
    2. Зарегистрировать бизнес
    3. Зарегистрировать сервис
    4. Найти сервис
    5. Использовать сервис
    6. Выйти
    > 1
    ===============================================
    Business Key: uddi:juddi.apache.org:node1
    Name: An Apache jUDDI Node
    Description: This is a UDDI registry node as implemented by Apache jUDDI.
    > 2
    Введите имя бизнеса
    test-business
    New business was created
    Key: 'uddi:juddi.apache.org:3072a386-b8c5-458c-82c7-107414017ed8'
    Name: 'test-business'
    > 3
    ===============================================
    Business Key: uddi:juddi.apache.org:node1
    Name: An Apache jUDDI Node
    Description: This is a UDDI registry node as implemented by Apache jUDDI.
    ===============================================
    Business Key: uddi:juddi.apache.org:3072a386-b8c5-458c-82c7-107414017ed8
    Name: test-business
    Description: 
    Введите ключ бизнеса
    uddi:juddi.apache.org:3072a386-b8c5-458c-82c7-107414017ed8
    Введите имя сервиса
    test-service
    Введите ссылку на wsdl
    http://localhost:8081/users?wsdl 
    Retrieving document at 'http://localhost:8081/users?wsdl'.
    Retrieving schema at 'http://localhost:8081/users?xsd=1', relative to 'http://localhost:8081/users?wsdl'.
    Services published from wsdl http://localhost:8081/users?wsdl
    -------------------------------------------
    Service Key: uddi:juddi.apache.org:3d32fdd9-8032-4a84-8788-536e31f1a197
    Owning Business Key: uddi:juddi.apache.org:3072a386-b8c5-458c-82c7-107414017ed8
    Name: Lang: null
    Value: test-service
    Lang: null
    Value: users
    Binding Key: uddi:juddi.apache.org:25c2ec05-e58e-4c9f-81ec-1bfbb82d7be2
    Access Point: http://localhost:8081/users?wsdl type wsdlDeployment
    Use this access point value as a URL to a WSDL document, which presumably will have a real access point defined.
    Binding Key: uddi:juddi.apache.org:5f005767-0dae-4800-a591-55c65324220d
    Access Point: http://localhost:8081/users type endPoint
    Use this access point value as an invocation endpoint.
    > 5
    Введите ключ сервиса
    uddi:juddi.apache.org:3d32fdd9-8032-4a84-8788-536e31f1a197
    Using endpoint 'http://localhost:8081/users'
    
    Выберите один из пунктов:
    0. Вывести help
    1. Вывести всех пользователей
    2. Применить фильтры
    3. Добавить пользователя
    4. Обновить информацию о пользователе
    5. Удалить пользователя
    6. Выйти
    > 1
    User{id=1, login='abc', email='abc@mail.ru', gender=true, registerDate=1998-01-01T00:00:00+03:00}
    User{id=2, login='def', email='def@mail.ru', gender=true, registerDate=1998-02-02T00:00:00+03:00}
    User{id=3, login='ghi', email='ghi@mail.ru', gender=true, registerDate=1998-03-03T00:00:00+03:00}