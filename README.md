In purpose of using redis server locally through docker container it needs to execute following commands in terminal:
1) 'docker compose up -d'
2) 'docker ps'
3) copy docker container name and write command: 'docker exec -it CONTAINER_NAME redis-cli'
4) Run some commands:
    - AUTH eYVX7EwVmmxKPCDmwMtyKVge8oLd2t81
    - keys * (to see all keys)