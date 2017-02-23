FROM mysql:5.7

MAINTAINER Vanei
LABEL description="Servidor MySQL" version="5.7"

EXPOSE 3306

RUN echo "\nlower_case_table_names = 1" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "default_storage_engine  = InnoDB" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "innodb_buffer_pool_size = 134217728" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "key_buffer_size         = 160M" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "max_allowed_packet      = 160M" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "thread_stack            = 192K" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "thread_cache_size       = 8" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "max_binlog_size         = 100M" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "innodb_write_io_threads = 32" >> /etc/mysql/mysql.conf.d/mysqld.cnf \
  && echo "innodb_file_per_table   = 0" >> /etc/mysql/mysql.conf.d/mysqld.cnf

