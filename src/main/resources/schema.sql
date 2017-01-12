create table followers(
	screen_name varchar not null,
	followers integer not null,
	timestamp timestamp not null,
	primary key(screen_name, timestamp));

create table users(
	screen_name varchar primary key not null);