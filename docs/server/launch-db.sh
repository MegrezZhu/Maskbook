mkdir -p /home/ubuntu/maskbook/db
docker run \
  -d \
  --name maskbook \
  -e MYSQL_ROOT_PASSWORD=PASS \
  -p 3306:3306 \
  -v /home/ubuntu/maskbook/db:/var/lib/mysql \
  mysql
