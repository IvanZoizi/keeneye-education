box.cfg{ listen = 3301, memtx_memory = 256 * 1024 * 1024 }

box.schema.user.create('app', {password = 'admin', if_not_exists = true})
box.schema.user.grant('app', 'super', nil, nil, {if_not_exists = true})

local jwt_info = box.schema.space.create('jwt_info', {
    if_not_exists = true,
    format = {
        {name = 'token_hash',  type = 'string'},
        {name = 'bucket_id',   type = 'unsigned', is_nullable = true},
        {name = 'user_id',     type = 'unsigned'},
        {name = 'token',       type = 'string'},
        {name = 'customer_id', type = 'unsigned'},
        {name = 'seller_id',   type = 'unsigned'},
        {name = 'email',       type = 'string'},
        {name = 'password',    type = 'string'},
        {name = 'deleted_at',  type = 'unsigned'},
        {name = 'expired_at',  type = 'unsigned'},
        {name = 'created_at',  type = 'unsigned'}
    }
})
jwt_info:create_index('primary', { parts = {{field = 'token_hash', type = 'string'}}, if_not_exists = true })
jwt_info:create_index('bucket_id', { parts = {{field = 'bucket_id', type = 'unsigned'}}, unique = false, if_not_exists = true })

local crud = require('crud')
crud.init_storage()
crud.init_router()