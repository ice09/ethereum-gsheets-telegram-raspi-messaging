// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

contract Switch {

    address creator;

    event SwitchTurned(address indexed sender, uint8 indexed state);

    constructor() {
        creator = msg.sender;
    }

    function turnSwitch(uint8 state) public {
        emit SwitchTurned(msg.sender, state);
    }

}