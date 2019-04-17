pragma solidity ^0.5.7

contract Authorization {

  struct User {
    bool authorized;
  }

  address public admin;

  mapping(address => User) public users;

  constructor() public {
    admin = msg.sender;
  }

  function authorize (address user) public {
    require(
      msg.sender == admin,
      "Only the admin can authorize users."
    );

    users[user].authorized = true;
  }

  function disauthorize (address user) public {
    require(
      msg.sender == admin,
      "Only the admin can disauthorize users."
    );

    users[user].authorized = false;
  }

  function isAuthorized(address user) public view
          returns (bool authorized_) {
    authorized_ = users[user].authorized;
  }



}
