import { ValidationError } from 'class-validator';

export function stringify (validateErr: ValidationError) {
  for (const msg of Object.values(validateErr.constraints)) {
    return msg;
  }
  return 'Unknown Error'; // shall never happened
}
