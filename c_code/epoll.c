#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <sys/epoll.h>

#define MAX_FD_NUM 1024  
#define MAXLEN 1024  

int buf_len = 0;

int main(int argc,char* argv[])
{
    int i = 0;
    printf("server start up\n");

    if(argc <= 2)
    {
        printf("usage:%s ip port\n",basename(argv[0]));
        return 1;
    }

    const char* ip = argv[1];
    int port = atoi(argv[2]);

    int server_sockfd = socket(PF_INET,SOCK_STREAM,0);

    struct sockaddr_in server_addr;
    bzero(&server_addr, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    inet_pton(AF_INET, ip, &server_addr.sin_addr);
    server_addr.sin_port = htons(port);

    int ret = bind(server_sockfd, (struct sockaddr*)&server_addr, sizeof(server_addr));
    assert(ret != -1);

    ret = listen(server_sockfd, MAX_FD_NUM - 1);
    assert(ret != -1);

    struct sockaddr_in client_addr;
    socklen_t client_addr_len = sizeof(struct sockaddr_in);

    // 创建一个 epfd，并且把 server_sockfd 注册到这个 epfd上。
    int epfd = epoll_create(1024);
    struct epoll_event ev,events[20];
    ev.data.fd = server_sockfd;
    ev.events = EPOLLIN;
    epoll_ctl(epfd, EPOLL_CTL_ADD, server_sockfd, &ev);

    int cur_fd_num = 1;
    char buf[MAXLEN]={0};

    while (1) {
        // nReady 就是 events 数组的长度。
        int nready = epoll_wait(epfd, events, 20, 50);

        int i = 0;
        for (; i < nready; i++) {
            if (events[i].data.fd == server_sockfd) {
                int client_sockfd = accept(server_sockfd,(struct sockaddr*)&client_addr,&client_addr_len);

                if(client_sockfd < 0) {
                    perror("accept");
                }
                else {
                    printf("accept client_addr %s\n",inet_ntoa(client_addr.sin_addr));
                    ev.data.fd = client_sockfd;
                    ev.events=EPOLLIN;
                    epoll_ctl(epfd, EPOLL_CTL_ADD, client_sockfd, &ev);
                }
            }
            else if (events[i].events & EPOLLIN) {
                int connfd = events[i].data.fd;
                int n = recv(connfd, buf, MAXLEN, 0);
                if(n < 0) {
                    if(ECONNRESET == errno) {
                        close(connfd);
                        epoll_ctl(epfd, EPOLL_CTL_DEL, connfd, 0);
                    }
                    else {
                        perror("recv");
                    }
                }

                printf("receive %s", buf);
                buf_len = n;

                ev.data.fd = connfd;
                ev.events = EPOLLOUT;
                epoll_ctl(epfd, EPOLL_CTL_MOD, connfd, &ev);
            }
            else if (events[i].events & EPOLLOUT) {
                int connfd = events[i].data.fd;
                write(connfd, buf, buf_len);

                ev.data.fd = connfd;
                ev.events = EPOLLIN;
                epoll_ctl(epfd, EPOLL_CTL_MOD, connfd, &ev);
            }
        }
    }

    return 0;
}

