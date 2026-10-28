local vshard = require('vshard')
local fiber = require('fiber')
fiber.create(function()
    while true do
        local ok = pcall(vshard.router.bootstrap, {timeout = 10, if_not_bootstrapped = true})
        if ok then break end
        fiber.sleep(1)
    end
end)