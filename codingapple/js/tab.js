var 버튼 = $('.tab-button');

$('.list').on('click',(e)=>{    
    console.log(e.target.dataset.id);
    탭열기(e.target.dataset.id);
});

function 탭열기(i){    
    버튼.removeClass('show');
    버튼.removeClass('orange');
    버튼.eq(i).addClass('show');
    버튼.eq(i).addClass('orange');    
};
// for(let i = 0; i < 버튼.length; i++){
//     버튼.eq(i).on('click', function () {    
//         버튼.removeClass('show');
//         버튼.removeClass('orange');
//         버튼.eq(i).addClass('show');
//         버튼.eq(i).addClass('orange');
//     });
// };


