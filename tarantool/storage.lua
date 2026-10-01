-- Схема создаётся идемпотентно, только когда инстанс стал rw
box.watch('box.status', function()
    if box.info.ro then return end

    local jwt = box.schema.space.create('jwt_info', {
        format = {
            {name = 'token_hash',  type = 'string'},
            {name = 'user_id',     type = 'unsigned'},
            {name = 'token',       type = 'string'},
            {name = 'customer_id', type = 'unsigned', is_nullable = true},
            {name = 'seller_id',   type = 'unsigned', is_nullable = true},
            {name = 'email',       type = 'string'},
            {name = 'password',    type = 'string'},
            {name = 'deleted_at',  type = 'unsigned', is_nullable = true},
            {name = 'expired_at',  type = 'unsigned'},
            {name = 'created_at',  type = 'unsigned'},
            {name = 'bucket_id',   type = 'unsigned', is_nullable = true},
        },
        if_not_exists = true,
    })
    jwt:create_index('primary', {
        parts = {{field = 'token_hash', type = 'string'}},
        if_not_exists = true,
    })
    jwt:create_index('bucket_id', {
        parts = {{field = 'bucket_id', type = 'unsigned'}},
        unique = false,
        if_not_exists = true,
    })
end)

-- Bootstrap vshard (для single-instance router)
local vshard = require('vshard')
local fiber = require('fiber')
fiber.create(function()
    while true do
        if pcall(vshard.router.bootstrap, {timeout = 15}) then break end
        fiber.sleep(0.5)
    end
end)