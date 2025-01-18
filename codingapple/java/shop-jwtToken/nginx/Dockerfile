FROM nginx:latest

COPY ./myconfig1.conf /etc/nginx/conf.d/myconfig1.conf
#기본 설정파일을 삭제해야 내 커스텀 설정파일에 덮어씌워지는 것을 예방
RUN rm /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]