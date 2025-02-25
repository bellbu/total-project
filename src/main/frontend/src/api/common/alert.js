import Swal from 'sweetalert2';
import withReactContent from 'sweetalert2-react-content';

const MySwal = withReactContent(Swal)

// 커스텀 스타일 설정
const customStyle = {
  title: {
    fontSize: '23px',    // 제목 폰트 크기
    fontWeight: '600'    // 제목 폰트 굵기
  },
  text: {
    fontSize: '18px'     // 내용 폰트 크기
  },
  confirmButton: {
    fontSize: '16px'     // 확인 버튼 폰트 크기
  },
  cancelButton: {
    fontSize: '16px'     // 취소 버튼 폰트 크기
  }
}

// 기본 alert
export const alert = (title, text, icon, callback) => {
    MySwal.fire({
        title: title,
        text: text,
        icon: icon,
        customClass: {
            title: 'swal2-title',
            htmlContainer: 'swal2-html-container',
            confirmButton: 'swal2-confirm',
        },
        backdrop: false,  // 배경 클릭 시 기본 동작 방지
        didOpen: () => {
            // 스타일 동적 적용
            const titleEl = document.querySelector('.swal2-title');
            const textEl = document.querySelector('.swal2-html-container');
            const confirmEl = document.querySelector('.swal2-confirm');

            if (titleEl) Object.assign(titleEl.style, customStyle.title);
            if (textEl) Object.assign(textEl.style, customStyle.text);
            if (confirmEl) Object.assign(confirmEl.style, customStyle.confirmButton);
        }
    })
    .then(callback)
}

// confirm
export const confirm = (title, text, icon, callback) => {
    MySwal.fire({
        title: title,
        text: text,
        icon: icon,
        showCancelButton: true,
        cancelButtonColor: "#d33",
        cancelButtonText: "취소",
        confirmButtonColor: "#3085d6",
        confirmButtonText: "확인",
        customClass: {
            title: 'swal2-title',
            htmlContainer: 'swal2-html-container',
            confirmButton: 'swal2-confirm',
            cancelButton: 'swal2-cancel'
        },
        backdrop: false,  // 배경 클릭 시 기본 동작 방지
        didOpen: () => {
            // 스타일 동적 적용
            const titleEl = document.querySelector('.swal2-title');
            const textEl = document.querySelector('.swal2-html-container');
            const confirmEl = document.querySelector('.swal2-confirm');
            const cancelEl = document.querySelector('.swal2-cancel');

            if (titleEl) Object.assign(titleEl.style, customStyle.title);
            if (textEl) Object.assign(textEl.style, customStyle.text);
            if (confirmEl) Object.assign(confirmEl.style, customStyle.confirmButton);
            if (cancelEl) Object.assign(cancelEl.style, customStyle.cancelButton);
        }
    })
    .then(callback)
}