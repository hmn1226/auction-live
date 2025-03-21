/**
 * 数値をカンマ区切りにするフォーマッター
 * @param {number|string} value - フォーマットする値
 * @returns {string} カンマ区切りされた文字列
 */
export const formatNumberWithCommas = (value) => {
  if (value === null || value === undefined || value === "") return "";
  
  // 文字列に変換し、既存のカンマを除去
  const numberValue = Number(value.toString().replace(/,/g, ""));
  
  // 数値でない場合は元の値を返す
  if (isNaN(numberValue)) return value;
  
  // カンマ区切りにフォーマット
  return numberValue.toLocaleString();
};

/**
 * 日時をフォーマットする関数
 * @param {string} dateString - 日時文字列
 * @param {boolean} includeTime - 時間を含めるかどうか
 * @returns {string} フォーマットされた日時文字列
 */
export const formatDateTime = (dateString, includeTime = true) => {
  if (!dateString) return "N/A";
  
  try {
    const date = new Date(dateString);
    
    // 無効な日付の場合
    if (isNaN(date.getTime())) return dateString;
    
    // 日付部分のフォーマット
    const dateOptions = { year: 'numeric', month: '2-digit', day: '2-digit' };
    
    // 時間を含める場合は時間のオプションを追加
    if (includeTime) {
      return date.toLocaleString('ja-JP', {
        ...dateOptions,
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      });
    }
    
    return date.toLocaleDateString('ja-JP', dateOptions);
  } catch (error) {
    console.error('日付フォーマットエラー:', error);
    return dateString;
  }
};
