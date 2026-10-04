box.schema.user.create('app', {password = 'admin', if_not_exists = true})
box.schema.user.grant('app', 'super', nil, nil, {if_not_exists = true})

local jwt_info = box.schema.space.create('jwt_info', {
    if_not_exists = true,
    format = {
        {name = 'token_hash',  type = 'string'},
        {name = 'bucket_id',   type = 'unsigned', is_nullable = true},
        {name = 'user_id',     type = 'unsigned'},
        {name = 'token',       type = 'string'},
        {name = 'customer_id', type = 'unsigned', is_nullable = true},
        {name = 'seller_id',   type = 'unsigned', is_nullable = true},
        {name = 'email',       type = 'string'},
        {name = 'password',    type = 'string'},
        {name = 'deleted_at',  type = 'unsigned', is_nullable = true},
        {name = 'expired_at',  type = 'unsigned', is_nullable = true},
        {name = 'created_at',  type = 'unsigned', is_nullable = true}
    }
})

local customers_cache = box.schema.space.create('customers_cache', {
    if_not_exists = true,
    format = {
        {name = 'id',        type = 'unsigned'},
        {name = 'bucket_id', type = 'unsigned'},
        {name = 'name',      type = 'string'},
        {name = 'surname',   type = 'string'}
    }
})

local sellers_cache = box.schema.space.create('sellers_cache', {
    if_not_exists = true,
    format = {
        {name = 'id',          type = 'unsigned'},
        {name = 'bucket_id',   type = 'unsigned'},
        {name = 'name',        type = 'string'},
        {name = 'surname',     type = 'string'},
        {name = 'address',     type = 'string'},
        {name = 'inn',         type = 'string'},
        {name = 'description', type = 'string'},
        {name = 'rating',      type = 'number', is_nullable = true}
    }
})

customers_cache:create_index('primary', {
    parts = {{field = 'id', type = 'unsigned'}},
    if_not_exists = true
})

customers_cache:create_index('bucket_id', {
    parts = {{field = 'bucket_id', type = 'unsigned'}},
    unique = false,
    if_not_exists = true
})

sellers_cache:create_index('primary', {
    parts = {{field = 'id', type = 'unsigned'}},
    if_not_exists = true
})

sellers_cache:create_index('bucket_id', {
    parts = {{field = 'bucket_id', type = 'unsigned'}},
    unique = false,
    if_not_exists = true
})

jwt_info:create_index('primary', {
    parts = {{field = 'token_hash', type = 'string'}},
    if_not_exists = true
})

jwt_info:create_index('bucket_id', {
    parts = {{field = 'bucket_id', type = 'unsigned'}},
    unique = false,
    if_not_exists = true
})