/**@file
 * Configuration constants
 */

/*
 *  Autosign (AP-scanning-based Attendance Signature System)
 *  Copyright (C) 2019, AutoSig team. <diyer175@hotmail.com>
 *
 *  This project is free software, you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License(GPL)
 *  as published by the Free Software Foundation, either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY, without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 */
 
 /*
 * Status codes
 */
module.exports = {
  EOK: 0,                  /* operation has been succeeded. */
  EFAULT: -1,              /* operation has failed. */
  EMISSING_PARAMEETR: -2,  /* missing parameetrs. */
  EINVALID_PARAMEETR: -3,  /* invalid parameter. */
  EUSER_NOT_FOUND: -4,     /* user not found. */
  EUSER_EXISTING: -5,      /* user is existing. */
  EINVALID_PASSWD: -6,     /* password is invalid */

  /*
   * MySQL database parameters
   */
  DATABASE_GLOBAL: "autosig_db",
  TABLE_USER_TEACHERS: "user_teachers",
  TABLE_SUMMARY_STUDENTS: "summary_students",
  TABLE_COURSES: "courses",
  TABLE_COURSE_SELECTION: "course_selection",
  TABLE_SIG_STATIS: "sig_statis",
  MYSQL_USERNAME: "root",
  MYSQL_PASSWD: "admin"
}
