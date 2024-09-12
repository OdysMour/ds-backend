# ds-backend

## Start postgres db as container

```bash
docker run --name ds-backend-pg --rm \
-e POSTGRES_PASSWORD=pass123 \
-e POSTGRES_USER=dbuser \
-e POSTGRES_DB=ds-backend \
-d -p 5432:5432 \
-v ds-backend-vol:/var/lib/postgresql/data \
postgres:14
```

## Stop the container

```bash
docker kill ds-backend-pg
```

## Delete volume

```bash
docker volume rm ds-backend-vol
```