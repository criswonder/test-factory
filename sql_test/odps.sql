
select 
  case app_version
    when '6.2.2' THEN 2
    when '6.2.3' THEN 3
    when '6.2.4' THEN 1
  else 4 END
from 
  idlefish_linyun_videoplayer_20180917
where 
 -- ds = '${bizdate}' 
   (arg1 = "Page_Video_Button-PlayExperience" or arg1 = "Page_Video_PlayExperience")

   LIMIT  1