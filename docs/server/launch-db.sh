mkdir -p /home/ubuntu/maskbook/db
docker run \
  -d \
  -v /home/ubuntu/maskbook/db:/var/lib/mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=PASS \
  -e MYSQL_DATABASE=maskbook \
  --name maskbook \
  mysql
