
A = AAD("3.5 5.6 -2; 4.5 6.7 9.4; 2.3 -2.3 9.4")

b = AAD("-0.2; 0.45; -3.4")

x= solve(A, b)
A*x-b



// test DGELS

var lssol = DGELS(A, b)
x


var ARowsMoreThanCols = AAD("3.5 5.6 -2; 4.5 6.7 9.4; 2.3 -2.3 9.4; 8.1 2.3 -0.2")
var bRowsMoreThanCols = AAD("-0.2; 0.45; -3.4; -0.3")

var lssolOverDetermined = DGELS(ARowsMoreThanCols, bRowsMoreThanCols)

/* Matlab
  ARowsMoreThanCols = [3.5 5.6 -2; 4.5 6.7 9.4; 2.3 -2.3 9.4; 8.1 2.3 -0.2];
  bRowsMoreThanCols = [-0.2; 0.45; -3.4; -0.3];
  lssolOverDetermined = ARowsMoreThanCols \ bRowsMoreThanCols
 */

 var AColsMoreThanRows= AAD("13.5  5.6  9.3 -2; 3.5 3.7 9.4 -0.7; 2.3 -2.3 0.9 9.4")
var bColsMoreThanRows= AAD("-0.4; 0.5; -3.4")

var lssolUnderDetermined = DGELS(AColsMoreThanRows, bColsMoreThanRows)

AColsMoreThanRows*lssolUnderDetermined
/* Matlab
  AColsMoreThanRows = [13.5  5.6  9.3 -2; 3.5 3.7 9.4 -0.7; 2.3 -2.3 0.9 9.4];
  bColsMoreThanRows = [-0.4; 0.5; -3.4; 9.7];
  lssolUnderDetermined = AColsMoreThanRows  \ bColsMoreThanRows
 */
 
