export function validateUsername(username) {
  if (!username) return "Ten dang nhap khong duoc de trong";
  if (username.length < 3) return "Ten dang nhap phai co it nhat 3 ky tu";
  if (username.length > 50) return "Ten dang nhap khong duoc vuot qua 50 ky tu";
  const regex = /^[a-zA-Z0-9._-]+$/;
  if (!regex.test(username)) return "Ten dang nhap chi duoc chua chu, so va ._-";
  return "";
}

export function validatePassword(password) {
  if (!password) return "Mat khau khong duoc de trong";
  if (password.length < 6) return "Mat khau phai co it nhat 6 ky tu";
  if (password.length > 100) return "Mat khau khong duoc vuot qua 100 ky tu";
  const regex = /^(?=.*[A-Za-z])(?=.*\d).+$/;
  if (!regex.test(password)) return "Mat khau phai co ca chu va so";
  return "";
}
