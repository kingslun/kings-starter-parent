external_url 'http://gitlab.cluster.local'
redis['enable'] = false
gitlab_rails['redis_host'] = 'redis.component'
gitlab_rails['redis_port'] = 6379
gitlab_rails['redis_password'] = 'kings@1995'
postgresql['enable'] = false
gitlab_rails['db_adapter'] = "postgresql"
gitlab_rails['db_encoding'] = "utf8"
gitlab_rails['db_host'] = 'postgres.component'
gitlab_rails['db_port'] = 5432
gitlab_rails['db_username'] = 'kings'
gitlab_rails['db_password'] = 'kings@1995'
gitlab_rails['db_database'] = 'gitlab'
puma['worker_processes'] = 0
sidekiq['concurrency'] = 10
prometheus_monitoring['enable'] = false

gitlab_rails['env'] = {
  'MALLOC_CONF' => 'dirty_decay_ms:1000,muzzy_decay_ms:1000'
}

gitaly['configuration'] = {
  concurrency: [
    {
      'rpc' => "/gitaly.SmartHTTPService/PostReceivePack",
      'max_per_repo' => 3,
    }, {
      'rpc' => "/gitaly.SSHService/SSHUploadPack",
      'max_per_repo' => 3,
    },
  ]
}
gitaly['env'] = {
  'MALLOC_CONF' => 'dirty_decay_ms:1000,muzzy_decay_ms:1000',
  'GITALY_COMMAND_SPAWN_MAX_PARALLEL' => '2'
}