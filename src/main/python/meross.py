import asyncio
import os
import sys
import logging

from meross_iot.http_api import MerossHttpClient
from meross_iot.manager import MerossManager
from meross_iot.model.http.exception import BadLoginException, HttpApiError
from meross_iot.model.enums import OnlineStatus
from meross_iot.model.http.error_codes import ErrorCodes

COMMAND_ON = "ON"
COMMAND_OFF = "OFF"

ENV_EMAIL = "MSS_EMAIL"
ENV_PASSWORD = "MSS_PASS"
ENV_DEVICE_NAME = "MSS_DEVICE_NAME"


async def main() -> int:
    # disable logging
    logging.getLogger("meross_iot").propagate = False
    logging.getLogger("meross_iot.manager").disabled = True

    email = os.environ.get(ENV_EMAIL) or ""
    password = os.environ.get(ENV_PASSWORD) or ""
    device_name = os.environ.get(ENV_DEVICE_NAME) or ""

    if len(email) <= 0 or len(password) <= 0:
        print("Authentication credentials missing")
        return 100

    if len(device_name) <= 0:
        print("Device name missing")
        return 101

    if len(sys.argv) < 2 or len(sys.argv[1]) <= 0:
        print("Command missing")
        return 102

    command = sys.argv[1]

    if command != COMMAND_ON and command != COMMAND_OFF:
        print("Invalid command")
        return 103
    print(email, password, device_name, command)
    try:
        http_api_client = await MerossHttpClient.async_from_user_password(
            api_base_url="https://iotx-eu.meross.com",
            email=email,
            password=password
        )
    except BadLoginException:
        print("Invalid credentials")
        return 104
    except HttpApiError as e:
        if ErrorCodes.CODE_WRONG_EMAIL == e.error_code or ErrorCodes.CODE_INVALID_EMAIL == e.error_code:
            print("Invalid credentials")
            return 104
        raise e

    # Setup and start the device manager
    manager = MerossManager(http_client=http_api_client)
    await manager.async_init()

    # Retrieve all the devices with name device_name
    await manager.async_device_discovery()
    all_devices = manager.find_devices(device_name=device_name)
    online_devices = list(filter(lambda d: d.online_status == OnlineStatus.ONLINE, all_devices))

    devices = all_devices if len(online_devices) < 1 else online_devices

    if len(devices) < 1:
        print("Device not found")
        # Close the manager and logout from http_api
        manager.close()
        await http_api_client.async_logout()
        return 105

    device = devices[0]
    # update device status
    await device.async_update()

    if device.online_status != OnlineStatus.ONLINE:
        print("Device offline")
        # Close the manager and logout from http_api
        manager.close()
        await http_api_client.async_logout()
        return 106

    if command == COMMAND_ON:
        await device.async_turn_on()
    elif command == COMMAND_OFF:
        await device.async_turn_off()

    # Close the manager and logout from http_api
    manager.close()
    await http_api_client.async_logout()
    return 0


if __name__ == "__main__":
    # Windows and python 3.8 requires to set up a specific event_loop_policy.
    #  On Linux and MacOSX this is not necessary.
    if os.name == "nt":
        asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())
    loop = asyncio.get_event_loop()
    exit_code = loop.run_until_complete(main())
    loop.stop()

    sys.exit(exit_code)
