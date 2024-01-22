//삭제 기능
const deleteBtn = document.getElementById('delete-btn');

if(deleteBtn){
    deleteBtn.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        // fetch(`/api/articles/${id}`, {
        //     method: 'DELETE'
        // }).then(()=>{
        //     alert("삭제 완료");
        //     location.replace('/articles');
        // })
        function success(){
            alert("삭제 완료")
            location.replace('/articles');
        }
        function fail(){
            alert("삭제 실패")
            location.replace('/articles');
        }

        httpRequest('DELETE', `/api/articles/${id}`, null, success,fail);

    });
}

const modifyBtn = document.getElementById('modify-btn');

if(modifyBtn){
    modifyBtn.addEventListener('click', function (){
        let params = new URLSearchParams(location.search)
        let id = params.get('id');

        body = JSON.stringify({
            title : document.getElementById('title').value,
            content : document.getElementById('content').value
        })


        function success(){
            alert("수정 완료")
            location.replace(`/articles/${id}`);
        }
        function fail(){
            alert("수정 실패")
            location.replace(`/articles/${id}`);
        }

        httpRequest('PUT', `/api/articles/${id}`, body, success,fail);
        // console.log(params);
        //
        // fetch(`/api/articles/${id}`, {
        //     method: 'PUT',
        //     headers : {
        //         "Content-Type" : "application/json",
        //     },
        //     body : JSON.stringify({
        //         title : document.getElementById('title').value,
        //         content : document.getElementById('content').value
        //     })
        // }).then(()=>{
        //     alert("수정 완료");
        //     location.replace(`/articles/${id}`);
        // })
    });
}

const createBtn =  document.getElementById('create-btn');

if(createBtn){
    createBtn.addEventListener('click', function (){

        body = JSON.stringify({
            title : document.getElementById('title').value,
            content : document.getElementById('content').value
        })


        function success(){
            alert("등록 완료")
            location.replace(`/articles`);
        }
        function fail(){
            alert("등록 실패")
            location.replace(`/articles`);
        }

        httpRequest('POST', `/api/articles`, body, success,fail);

        // fetch('/api/articles', {
        //     method: 'POST',
        //     headers : {
        //         "Content-Type" : "application/json",
        //     },
        //     body : JSON.stringify({
        //         title : document.getElementById('title').value,
        //         content : document.getElementById('content').value
        //     })
        // }).then(()=>{
        //     alert("등록 완료");
        //     location.replace("/articles");
        // })
    });
}



function httpRequest(method, url,body,success,fail){
    fetch(url, {
        method : method,
        headers : {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if(response.status === 200 || response.status === 201){
            return success();
        }
        const refresh_token = getCookie('refresh_token');
        if(response.status === 401 && refresh_token){
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            }).then(res =>{
                if(res.ok){
                    return res.json();
                }
            }).then(result => {
                localStorage.setItem('access_token', result.accessToken);
                httpRequest(method, url, body,success,fail);
            }).catch(error => fail());
        }else {
            return fail();
        }
    })
}

function getCookie(key){
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item){
        item = item.replace(' ', '');

        var dic = item.split('=');
        if(key === dic[0]){
            result = dic[1];
            return true;
        }
    });

    return result;
}





