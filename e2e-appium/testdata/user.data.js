module = module.exports = {
  validUser: {
    username: 'testuser@vibecraft.com',
    password: 'Password123!',
    displayName: 'Test Automation User',
  },
  invalidCredentials: [
    { username: 'nonexistent@vibecraft.com', password: 'WrongPassword123!', expectedError: 'Invalid credentials' },
    { username: 'testuser@vibecraft.com', password: 'BadPassword', expectedError: 'Invalid credentials' },
  ],
  boundaryCases: [
    { username: '', password: 'Password123!', scenario: 'Empty Username' },
    { username: 'testuser@vibecraft.com', password: '', scenario: 'Empty Password' },
    { username: '', password: '', scenario: 'Empty Username and Password' },
  ],
};
