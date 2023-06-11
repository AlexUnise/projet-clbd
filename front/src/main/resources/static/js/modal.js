/**
 * @author Alexandre Ouenadio
 * @description Handles the modals' logic like the opening and closing of the create vehicle form or the update vehicle form.
 */
function loadModal(){

    const overlay = document.getElementById('overlay');
    
    document.querySelectorAll('[data-modal-open]').forEach((btn) =>{
        
        btn.addEventListener('click', (e) =>{
            
            //Look for the matching modal
            const modal = document.querySelector(btn.dataset.modalOpen);
    
            overlay.classList.add('active');
            modal.classList.add("active");
    
        })
    })
    
    
    document.querySelectorAll('[data-modal-close]').forEach((btn) =>{
        
        btn.addEventListener('click', (e) =>{
    
            //Look for the matching modal
            const modal = e.currentTarget.closest('.modal');
    
            modal.classList.remove("active");
            overlay.classList.remove('active');
    
        })
    })
    
    
    console.log("Modal loaded");
    

}    


