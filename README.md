Construir imagen con build
´docker build -t ms-eventos .´
Login a DockerHub
´docker login´
Etiquetar imagen para docker hub
´docker tag ms-eventos merooook/ms-eventos:v1´
Subir imagen a docker hub
´docker push merooook/ms-eventos:v1´