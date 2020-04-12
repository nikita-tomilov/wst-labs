## Prerequisites:
- docker, docker-compose, psql installed
 
## How-to:
- YOU ABSOLUTELY HAVE TO mvn clean package OTHERWISE IDEA WILL BE USELESS
- ./run.sh
- run standalone/tests/AppLauncher.java via Idea
- You should be able to run client/App.java via Idea and be able to connect to Stanalone server

## Postgre
After run.sh has done its job and the container is running,
you should be able to view the table contents:

    hotaro@hotaro-megastation:~$ psql -h localhost -U admin wst1_db
    Password for user admin: <type "admin" here>
    psql (10.12 (Ubuntu 10.12-0ubuntu0.18.04.1), server 12.2 (Debian 12.2-2.pgdg100+1))
    WARNING: psql major version 10, server major version 12.
            Some psql features might not work.
    Type "help" for help.
    
    wst1_db=> \dt
          List of relations
    Schema | Name  | Type  | Owner 
    --------+-------+-------+-------
    public | users | table | admin
    (1 row)
    
    wst1_db=> select * from users;
    id | login | password |    email    | gender | register_date 
    ----+-------+----------+-------------+--------+---------------
     1 | abc   | 1234     | abc@mail.ru | t      | 1998-01-01
     2 | def   | 1234     | def@mail.ru | t      | 1998-02-02
     3 | ghi   | qwerty   | ghi@mail.ru | t      | 1998-03-03
    (3 rows)
    
    wst1_db=>

## Rebuilding
If the containers do not accept the changes you made to sources, just kill
them and do run.sh from scratch:

    $ docker-compose rm postgre
    
## Example output

Connected to the target VM, address: '127.0.0.1:53273', transport: 'socket'

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
    > 5
    Введите id пользователя, которого хотите удалить: 2
    1
    > 1
    User{id=1, login='abc', email='abc@mail.ru', gender=true, registerDate=1998-01-01T00:00:00+03:00}
    User{id=3, login='ghi', email='ghi@mail.ru', gender=true, registerDate=1998-03-03T00:00:00+03:00}
    > 
    
In server logs: "AUTH USING abc 1234"