**Приложение Filmorate**\

**Описание**\
Filmorate это сервис, который работает с фильмами и рейтингами пользователей, и возвращает топ-5 фильмов, рекомендованных к просмотру. 

**Функциональность**\
Приложение выполняет следующие основные функции: 
* Приложение позволяет регистрироваться новым пользователям.
* Добавлять пользователей в друзья. 
* Добавлять пользователям фильтмы. 
* Оценивать фильмы. 
* Оствлять комментарии. 

**ER-диаграмма проекта Filmorate**
![er-diagram](er_diagram_filmorate.png)  

Диаграмма описывает структуру хранения данных приложения. Основные сущности приложения:
* Films - фильмы
* Users - пользователи

**Примеры запросов SQL**\
_Получение списка всех фильмов:_

<pre>SELECT *
FROM films; 
</pre>
_Получение информации о пользователе с ID = 1:_

<pre>SELECT *
FROM users
WHERE users.user_id = 1;
</pre>  
_Сортировка фильмов по жанрам:_
<pre>SELECT g.genre_name,
       f.film_name      	
FROM films AS f
JOIN film_genres AS fg ON f.film_id  = fg.film_id
JOIN genres AS g ON fg.genre_id = g.genre_id 
order by g.genre_name; 
</pre>

_Получение списка фильмов, которым поставил лайк пользователь. В имени пользователя первая буква "Д":_
<pre>SELECT *
FROM (SELECT f.film_name AS f_name, 
			 u.user_name AS u_name 
	  FROM films f JOIN film_likes fl ON f.film_id = fl.film_id 
	               JOIN users u ON fl.user_id = u.user_id) AS fu 
WHERE substr(u_name, 1 , 1) = 'Д'; 
</pre>
