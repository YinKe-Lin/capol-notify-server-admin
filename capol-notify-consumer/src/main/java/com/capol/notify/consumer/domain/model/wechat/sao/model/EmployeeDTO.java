package com.capol.notify.consumer.domain.model.wechat.sao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String birthday;
    private String address;
    private String mobilePhone;
    private String telPhone;
    private String email;
    private String sex;
    private String type;
    private String description;
    private Long deptId;
    private String companyName;
    private String deptName;
    private String title;
    private String jobNo;
    private String objectSid;
    private String available;
    private String sapEmpId;
    private String sapDeptId;
    private Long enterpriseId;
    private Date loginTime;

    public String toString() {
        return "EmployeeDTO{username='" + this.username + '\'' + ", password='" + this.password + '\''
                + ", name='" + this.name + '\'' + ", birthday='" + this.birthday + '\''
                + ", address='" + this.address + '\'' + ", mobilePhone='" + this.mobilePhone + '\''
                + ", telPhone='" + this.telPhone + '\'' + ", email='" + this.email + '\'' + ", sex='" + this.sex + '\''
                + ", type='" + this.type + '\'' + ", description='" + this.description + '\''
                + ", deptId=" + this.deptId + ", companyName='" + this.companyName + '\''
                + ", deptName='" + this.deptName + '\'' + ", title='" + this.title + '\''
                + ", jobNo='" + this.jobNo + '\'' + ", objectSid='" + this.objectSid + '\''
                + ", id='" + this.getId() + '\'' + '}';
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EmployeeDTO)) {
            return false;
        } else {
            EmployeeDTO other = (EmployeeDTO) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$username = this.getUsername();
                Object other$username = other.getUsername();
                if (this$username == null) {
                    if (other$username != null) {
                        return false;
                    }
                } else if (!this$username.equals(other$username)) {
                    return false;
                }

                Object this$password = this.getPassword();
                Object other$password = other.getPassword();
                if (this$password == null) {
                    if (other$password != null) {
                        return false;
                    }
                } else if (!this$password.equals(other$password)) {
                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                label254:
                {
                    Object this$birthday = this.getBirthday();
                    Object other$birthday = other.getBirthday();
                    if (this$birthday == null) {
                        if (other$birthday == null) {
                            break label254;
                        }
                    } else if (this$birthday.equals(other$birthday)) {
                        break label254;
                    }

                    return false;
                }

                label247:
                {
                    Object this$address = this.getAddress();
                    Object other$address = other.getAddress();
                    if (this$address == null) {
                        if (other$address == null) {
                            break label247;
                        }
                    } else if (this$address.equals(other$address)) {
                        break label247;
                    }

                    return false;
                }

                Object this$mobilePhone = this.getMobilePhone();
                Object other$mobilePhone = other.getMobilePhone();
                if (this$mobilePhone == null) {
                    if (other$mobilePhone != null) {
                        return false;
                    }
                } else if (!this$mobilePhone.equals(other$mobilePhone)) {
                    return false;
                }

                label233:
                {
                    Object this$telPhone = this.getTelPhone();
                    Object other$telPhone = other.getTelPhone();
                    if (this$telPhone == null) {
                        if (other$telPhone == null) {
                            break label233;
                        }
                    } else if (this$telPhone.equals(other$telPhone)) {
                        break label233;
                    }

                    return false;
                }

                label226:
                {
                    Object this$email = this.getEmail();
                    Object other$email = other.getEmail();
                    if (this$email == null) {
                        if (other$email == null) {
                            break label226;
                        }
                    } else if (this$email.equals(other$email)) {
                        break label226;
                    }

                    return false;
                }

                Object this$sex = this.getSex();
                Object other$sex = other.getSex();
                if (this$sex == null) {
                    if (other$sex != null) {
                        return false;
                    }
                } else if (!this$sex.equals(other$sex)) {
                    return false;
                }

                Object this$type = this.getType();
                Object other$type = other.getType();
                if (this$type == null) {
                    if (other$type != null) {
                        return false;
                    }
                } else if (!this$type.equals(other$type)) {
                    return false;
                }

                label205:
                {
                    Object this$description = this.getDescription();
                    Object other$description = other.getDescription();
                    if (this$description == null) {
                        if (other$description == null) {
                            break label205;
                        }
                    } else if (this$description.equals(other$description)) {
                        break label205;
                    }

                    return false;
                }

                label198:
                {
                    Object this$deptId = this.getDeptId();
                    Object other$deptId = other.getDeptId();
                    if (this$deptId == null) {
                        if (other$deptId == null) {
                            break label198;
                        }
                    } else if (this$deptId.equals(other$deptId)) {
                        break label198;
                    }

                    return false;
                }

                Object this$companyName = this.getCompanyName();
                Object other$companyName = other.getCompanyName();
                if (this$companyName == null) {
                    if (other$companyName != null) {
                        return false;
                    }
                } else if (!this$companyName.equals(other$companyName)) {
                    return false;
                }

                label184:
                {
                    Object this$deptName = this.getDeptName();
                    Object other$deptName = other.getDeptName();
                    if (this$deptName == null) {
                        if (other$deptName == null) {
                            break label184;
                        }
                    } else if (this$deptName.equals(other$deptName)) {
                        break label184;
                    }

                    return false;
                }

                Object this$title = this.getTitle();
                Object other$title = other.getTitle();
                if (this$title == null) {
                    if (other$title != null) {
                        return false;
                    }
                } else if (!this$title.equals(other$title)) {
                    return false;
                }

                label170:
                {
                    Object this$jobNo = this.getJobNo();
                    Object other$jobNo = other.getJobNo();
                    if (this$jobNo == null) {
                        if (other$jobNo == null) {
                            break label170;
                        }
                    } else if (this$jobNo.equals(other$jobNo)) {
                        break label170;
                    }

                    return false;
                }

                Object this$objectSid = this.getObjectSid();
                Object other$objectSid = other.getObjectSid();
                if (this$objectSid == null) {
                    if (other$objectSid != null) {
                        return false;
                    }
                } else if (!this$objectSid.equals(other$objectSid)) {
                    return false;
                }

                Object this$available = this.getAvailable();
                Object other$available = other.getAvailable();
                if (this$available == null) {
                    if (other$available != null) {
                        return false;
                    }
                } else if (!this$available.equals(other$available)) {
                    return false;
                }

                Object this$sapEmpId = this.getSapEmpId();
                Object other$sapEmpId = other.getSapEmpId();
                if (this$sapEmpId == null) {
                    if (other$sapEmpId != null) {
                        return false;
                    }
                } else if (!this$sapEmpId.equals(other$sapEmpId)) {
                    return false;
                }

                label142:
                {
                    Object this$sapDeptId = this.getSapDeptId();
                    Object other$sapDeptId = other.getSapDeptId();
                    if (this$sapDeptId == null) {
                        if (other$sapDeptId == null) {
                            break label142;
                        }
                    } else if (this$sapDeptId.equals(other$sapDeptId)) {
                        break label142;
                    }

                    return false;
                }

                label135:
                {
                    Object this$enterpriseId = this.getEnterpriseId();
                    Object other$enterpriseId = other.getEnterpriseId();
                    if (this$enterpriseId == null) {
                        if (other$enterpriseId == null) {
                            break label135;
                        }
                    } else if (this$enterpriseId.equals(other$enterpriseId)) {
                        break label135;
                    }

                    return false;
                }

                Object this$loginTime = this.getLoginTime();
                Object other$loginTime = other.getLoginTime();
                if (this$loginTime == null) {
                    if (other$loginTime != null) {
                        return false;
                    }
                } else if (!this$loginTime.equals(other$loginTime)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EmployeeDTO;
    }

    public int hashCode() {
        int result = 1;
        Object $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $birthday = this.getBirthday();
        result = result * 59 + ($birthday == null ? 43 : $birthday.hashCode());
        Object $address = this.getAddress();
        result = result * 59 + ($address == null ? 43 : $address.hashCode());
        Object $mobilePhone = this.getMobilePhone();
        result = result * 59 + ($mobilePhone == null ? 43 : $mobilePhone.hashCode());
        Object $telPhone = this.getTelPhone();
        result = result * 59 + ($telPhone == null ? 43 : $telPhone.hashCode());
        Object $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        Object $sex = this.getSex();
        result = result * 59 + ($sex == null ? 43 : $sex.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Object $deptId = this.getDeptId();
        result = result * 59 + ($deptId == null ? 43 : $deptId.hashCode());
        Object $companyName = this.getCompanyName();
        result = result * 59 + ($companyName == null ? 43 : $companyName.hashCode());
        Object $deptName = this.getDeptName();
        result = result * 59 + ($deptName == null ? 43 : $deptName.hashCode());
        Object $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        Object $jobNo = this.getJobNo();
        result = result * 59 + ($jobNo == null ? 43 : $jobNo.hashCode());
        Object $objectSid = this.getObjectSid();
        result = result * 59 + ($objectSid == null ? 43 : $objectSid.hashCode());
        Object $available = this.getAvailable();
        result = result * 59 + ($available == null ? 43 : $available.hashCode());
        Object $sapEmpId = this.getSapEmpId();
        result = result * 59 + ($sapEmpId == null ? 43 : $sapEmpId.hashCode());
        Object $sapDeptId = this.getSapDeptId();
        result = result * 59 + ($sapDeptId == null ? 43 : $sapDeptId.hashCode());
        Object $enterpriseId = this.getEnterpriseId();
        result = result * 59 + ($enterpriseId == null ? 43 : $enterpriseId.hashCode());
        Object $loginTime = this.getLoginTime();
        result = result * 59 + ($loginTime == null ? 43 : $loginTime.hashCode());
        return result;
    }
}
