module = module.exports = {
  validFormData: {
    fullName: 'John Mobile QA',
    email: 'john.qa@example.com',
    phone: '+15551234567',
    password: 'SecurePassword123!',
    confirmPassword: 'SecurePassword123!',
    gender: 'Male',
    acceptTerms: true,
  },
  invalidEmails: [
    'plainaddress',
    '@missinguser.com',
    'user@.com',
    'user@domain..com',
  ],
  invalidPhones: [
    '123',
    'abcdefghij',
    '++123456',
  ],
  weakPasswords: [
    { pwd: '123', reason: 'Too short (min 8 chars)' },
    { pwd: 'password', reason: 'Missing uppercase and special character' },
    { pwd: 'ALLUPPERCASE', reason: 'Missing lowercase and digits' },
  ],
};
