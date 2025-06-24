# ultra-decentralised-money-api

### How to Setup local Postgres for dev

Init local dev psql db

`createuser --superuser postgres`

`psql -U postgres`

Then create db:

```
CREATE USER udm PASSWORD 'password';

CREATE DATABASE udm WITH OWNER udm;
```
